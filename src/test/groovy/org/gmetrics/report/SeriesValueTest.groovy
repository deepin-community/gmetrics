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

import org.gmetrics.test.AbstractTestCase

/**
 * Tests for SeriesValue
 *
 * @author Chris Mair
 * @version $Revision: 119 $ - $Date: 2010-07-11 21:56:11 -0400 (Sun, 11 Jul 2010) $
 */
class SeriesValueTest extends AbstractTestCase {

    void testConstructorInitializesValues() {
        def seriesValue = new SeriesValue('sample', 789)
        assert seriesValue.name == 'sample'
        assert seriesValue.value == 789
    }

}
