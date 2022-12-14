/*
 * Copyright 2009 the original author or authors.
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
package org.gmetrics.result

/**
 * Represents the results for a single metric for a single class
 *
 * @author Chris Mair
 * @version $Revision: 180 $ - $Date: 2011-11-27 21:56:48 -0500 (Sun, 27 Nov 2011) $
 */
class ClassMetricResult {
    MetricResult classMetricResult
    Map<MethodKey,MetricResult> methodMetricResults = [:]

    ClassMetricResult(MetricResult metricResult, Map<MethodKey,MetricResult> methodMetricResults=null) {
        this.classMetricResult = metricResult
        this.methodMetricResults = methodMetricResults
    }

    String toString() {
        return "ClassMetricResult[classMetricResult=$classMetricResult, methodMetricResults=$methodMetricResults]"
    }
}