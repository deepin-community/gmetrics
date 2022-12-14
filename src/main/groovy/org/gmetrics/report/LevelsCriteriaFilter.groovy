/*
 * Copyright 2010 the original author or authors.
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
package org.gmetrics.report

import org.gmetrics.metric.Metric
import org.gmetrics.metric.MetricLevel

/**
 * Provides data and behavior for enabling reports to filter the set of metrics included in a report.
 * This class is intended to be used as a Groovy @Mixin for ReportWriter classes.
 *
 * @author Chris Mair
 * @version $Revision: 118 $ - $Date: 2010-07-10 21:55:10 -0400 (Sat, 10 Jul 2010) $
 */
class LevelsCriteriaFilter {

    private Map levelsCriteriaMap

    void setLevels(String criteria) {
        levelsCriteriaMap = MetricCriteriaFilterHelper.parseCriteria(criteria)
    }

    boolean includesLevel(Metric metric, MetricLevel level) {
        return MetricCriteriaFilterHelper.includesName(levelsCriteriaMap, metric, level.name)
    }

}