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
package org.gmetrics.metric.coverage

/**
 * Tests for CoberturaLineCoverageMetric
 *
 * @author Chris Mair
 */
class CoberturaLineCoverageMetricTest extends AbstractCoberturaMetricTestCase {

    static metricClass = CoberturaLineCoverageMetric

    private static final CHANNEL_VALUE = (14 / 16 as BigDecimal).setScale(2, BigDecimal.ROUND_HALF_UP)
    private static final EMAIL_VALUE = 0.66
    private static final SERVICE_PACKAGE_VALUE = 0.85

    // Implement abstract method using Groovy Properties
    BigDecimal rootPackageValue = 0.95
    BigDecimal servicePackageValue = SERVICE_PACKAGE_VALUE

    //------------------------------------------------------------------------------------
    // Tests
    //------------------------------------------------------------------------------------

    void testHasProperName() {
        assert metric.name == 'CoberturaLineCoverage'
    }

    // Tests for applyToMethod()

    void testApplyToMethod_EnabledIsFalse_ReturnsNull() {
        final SOURCE = """
            package com.example.service
            class Email {
                String toString() { }
            }
        """
        metric.enabled = false
        assert applyToMethod(SOURCE) == null
    }

    void testApplyToMethod() {
        final SOURCE = """
            package com.example.service
            class Email {
                String toString() { }
            }
        """
        assert applyToMethodValue(SOURCE) == 0.99
    }

    // Tests for calculate()

    void testCalculate() {
        final SOURCE = """
            package com.example.service
            class Email {
                String toString() { }
            }
        """
        assert calculateForMethod(SOURCE) == 0.99
    }

    void testCalculate_MethodThatHasNoCoverageInformation() {
        final SOURCE = """
            package com.example.service
            class Email {
                int unknown() { }
            }
        """
        assertCalculateForMethodReturnsNull(SOURCE)
    }

    void testCalculate_ReturnsNullForAbstractMethodDeclaration() {
        final SOURCE = """
            package com.example.service
            class Email {
                abstract String getId()
            }
        """
        assertCalculateForMethodReturnsNull(SOURCE)
    }

    void testCalculate_Constructor() {
        final SOURCE = """
            package com.example.service
            class Context {
                Context(Collection stuff) { }
            }
        """
        assert calculateForConstructor(SOURCE) == 0.22
    }

    // Tests for applyToClass()

    void testApplyToClass_ClassWithNoMethods() {
        final SOURCE = """
            package com.example.service
            class Email { }
        """
        assertApplyToClass(SOURCE, EMAIL_VALUE, 0)
    }

    void testApplyToClass_ClassWithOneMethod() {
        final SOURCE = """
            package com.example.service
            class Email {
                String toString() { }
            }
        """
        assertApplyToClass(SOURCE, EMAIL_VALUE, 0.99, ['String toString()':0.99])
    }

    void testApplyToClass_ClassWithMethodThatHasNoCoverageInformation() {
        final SOURCE = """
            package com.example.service
            class Email {
                int unknown() { }
            }
        """
        assertApplyToClass(SOURCE, EMAIL_VALUE, 0)
    }

    void testApplyToClass_IgnoresAbstractMethods() {
        final SOURCE = """
            package com.example.service
            class Email {
                abstract String getId()
            }
        """
        assertApplyToClass(SOURCE, EMAIL_VALUE, 0)
    }

    void testApplyToClass_Constructor() {
        final SOURCE = """
            package com.example.service
            class Context {
                Context(Collection stuff) { }
            }
        """
        assertApplyToClass(SOURCE, 0.11, 0.22, ['void <init>(Collection)':0.22])
    }

    void testApplyToClass_OverloadedConstructor() {
        final SOURCE = """
            package com.example.service
            class MyException {
                MyException(String name) { }
                MyException(String name, String id) { }
                MyException(String name, Throwable cause) { }
                MyException(Throwable cause) { }
            }
        """
        assertApplyToClass(SOURCE, 0.66, 0.45, [
            'void <init>(String)':0.3,
            'void <init>(String, String)':0.4,
            'void <init>(String, Throwable)':0.5,
            'void <init>(Throwable)':0.6,
        ])
    }

    void testApplyToClass_ContainsInnerClasses() {
        final SOURCE = """
            package com.example.model
            class Channel {
                Channel(String name, int count, String code) { }
                static Channel parse(String text) { }
                String getId() { }
            }
        """
        assertApplyToClass(SOURCE, CHANNEL_VALUE, 0.8, [
            'void <init>(String, int, String)':0.9,
            'Channel parse(String)':0.7,
            'String getId()':0.8])
    }

    // Tests for getCoverageRatioForClass

    void testGetCoverageRatioForClass() {
        assertRatio(metric.getCoverageRatioForClass('com.example.service.MyException'), 16, 24)
    }

    void testGetCoverageRatioForClass_ClassContainingClosures() {
        assertRatio(metric.getCoverageRatioForClass('com.example.model.Channel'), 14, 16)
    }

    void testGetCoverageRatioForClass_EmptyClass() {
        assertRatio(metric.getCoverageRatioForClass('com.example.service.ClientMappingDao'), 0, 0)
    }

    void testGetCoverageRatioForClass_NoSuchClass_ReturnsNull() {
        assert metric.getCoverageRatioForClass('NoSuchClass') == null
    }

}