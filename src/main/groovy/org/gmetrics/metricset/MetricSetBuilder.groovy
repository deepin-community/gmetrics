/*
 * Copyright 2012 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gmetrics.metricset

import org.gmetrics.metric.Metric
import org.gmetrics.metricregistry.MetricRegistryHolder

/**
 * A Builder for MetricSets. Create a MetricSet by calling the <code>metricset</code>
 * method, passing in a <code>Closure</code> defining the contents of the MetricSet.
 * The <code>Closure</code> can contain any combination of the following (as well as
 * arbitrary Groovy code):
 * <ul>
 *   <li><code>metricset</code> - to load a MetricSet file. The path specifies a Groovy file.</li>
 *   <li><code>metric</code> - to load a single Metric; specify the Metric class</li>
 *   <li><i>The name of a predefined Metric</i> - to load a single Metric</li>
 *   <li><code>description</code> - description of the MetricSet (optional)</li>
 * </ul>
 *
 * @author Chris Mair
 */
class MetricSetBuilder {

    private topLevelDelegate = new TopLevelDelegate()

    void metricset(Closure closure) {
        closure.delegate = topLevelDelegate
        closure.call()
    }

    MetricSet getMetricSet() {
        topLevelDelegate.metricSet
    }
}

class TopLevelDelegate {
    private allMetricSet = new CompositeMetricSet()
    private nestedLevel = 0

    void metricset(String path) {
        def metricSet = new GroovyDslMetricSet(path)
        allMetricSet.addMetricSet(metricSet)
    }

    void metricset(String path, Closure closure) {
        def metricSet = new GroovyDslMetricSet(path)
        def metricSetConfigurer = new MetricSetDelegate(metricSet)
        closure.delegate = metricSetConfigurer
        closure.setResolveStrategy(Closure.DELEGATE_FIRST)
        nestedLevel++
        closure.call()
        nestedLevel--
        allMetricSet.addMetricSet(metricSetConfigurer.metricSet)
    }

    Metric metric(Class metricClass) {
        assertClassImplementsMetricInterface(metricClass)
        Metric metric = metricClass.newInstance()
        return addMetric(metric)
    }

    Metric metric(Class metricClass, Map properties) {
        assertClassImplementsMetricInterface(metricClass)
        Metric metric = metricClass.newInstance()
        properties.each { key, value -> metric[key] = value }
        return addMetric(metric)
    }

    Metric metric(Class metricClass, Closure closure) {
        assertClassImplementsMetricInterface(metricClass)
        Metric metric = metricClass.newInstance()
        closure.delegate = metric
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        nestedLevel++
        closure.call()
        nestedLevel--
        return addMetric(metric)
    }

    Metric propertyMissing(String name) {
        def metricClass = MetricRegistryHolder.metricRegistry?.getMetricClass(name)
        assert metricClass, "No such metric named [$name]"
        metric(metricClass)
    }

    Metric methodMissing(String name, args) {
        def metricClass = MetricRegistryHolder.metricRegistry?.getMetricClass(name)
        assert metricClass, "No such metric named [$name]"
        if (args.size() > 0) {
            metric(metricClass, args[0])
        }
        else {
            metric(metricClass)
        }
    }

    @SuppressWarnings(['EmptyMethod', 'UnusedMethodParameter'])
    void description(String description) {
        // Do nothing
    }

    protected MetricSet getMetricSet() {
        return allMetricSet
    }

    private void assertClassImplementsMetricInterface(Class metricClass) {
        assert metricClass
        assert Metric.isAssignableFrom(metricClass), "The metric class [${metricClass.name}] does not implement the org.gmetrics.metric.Metric interface"
    }

    private Metric addMetric(Metric metric) {
        if (isNotWithinAnotherMetricDefinition()) {
            allMetricSet.addMetric(metric)
        }
        return metric
    }

    private boolean isNotWithinAnotherMetricDefinition() {
        return nestedLevel == 0
    }
}

class MetricSetDelegate {
    MetricSet metricSet

    MetricSetDelegate(MetricSet metricSet) {
        this.metricSet = metricSet
    }

    def methodMissing(String name, args) {
        def metric = findMetric(name)
        assert metric, "No such metric named [$name]"

        def arg = args[0]
        if (arg instanceof Closure) {
            arg.delegate = metric
            arg.setResolveStrategy(Closure.DELEGATE_FIRST)
            arg.call()
        }
        else {
            // Assume it is a Map
            arg.each { key, value -> metric[key] = value }
        }
    }

    private Metric findMetric(String name) {
        metricSet.metrics.find { metric -> metric.name == name }
    }
}