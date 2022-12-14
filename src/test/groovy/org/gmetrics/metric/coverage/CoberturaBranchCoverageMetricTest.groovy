/*
 * Copyright 2011 the original author or authors.
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
 * Tests for CoberturaBranchCoverageMetric
 *
 * @author Chris Mair
 */
class CoberturaBranchCoverageMetricTest extends AbstractCoberturaMetricTestCase {

    static metricClass = CoberturaBranchCoverageMetric

    private static final EMAIL_VALUE = 0.61

    // Implement abstract method using Groovy Properties
    BigDecimal rootPackageValue = 0.79
    BigDecimal servicePackageValue = 0.65

    //------------------------------------------------------------------------------------
    // Tests
    //------------------------------------------------------------------------------------

    void testHasProperName() {
        assert metric.name == 'CoberturaBranchCoverage'
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
        assert applyToMethodValue(SOURCE) == 0.91
    }

    // Tests for calculate()

    void testCalculate() {
        final SOURCE = """
            package com.example.service
            class Email {
                String toString() { }
            }
        """
        assert calculateForMethod(SOURCE) == 0.91
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
        assert calculateForConstructor(SOURCE) == 0.32
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
        assertApplyToClass(SOURCE, EMAIL_VALUE, 0.91, ['String toString()':0.91])
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
        assertApplyToClass(SOURCE, 0.31, 0.32, ['void <init>(Collection)':0.32])
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
        assertApplyToClass(SOURCE, 0.61, 0.5, [
            'void <init>(String)':0.35,
            'void <init>(String, String)':0.45,
            'void <init>(String, Throwable)':0.55,
            'void <init>(Throwable)':0.65,
        ])
    }

    void testApplyToClass_ContainsInnerClasses() {
        final SOURCE = """
            package com.example.service
            class GenericLookupService {
                Map buildReverseLookupMap(Map map) { }
                Map get() { }
                Object getAllEnabledClients() { }
            }
        """
        assertApplyToClass(SOURCE, 0.92, 0.6, [
            'Map buildReverseLookupMap(Map)':0.8,
            'Map get()':0.5,
            'Object getAllEnabledClients()':0.5])
    }

    // Tests for getCoverageRatioForClass

    void testGetCoverageRatioForClass() {
        assertRatio(metric.getCoverageRatioForClass('com.example.service.Email'), 2, 6)
    }

    void testGetCoverageRatioForClass_ClassContainingClosures() {
        assertRatio(metric.getCoverageRatioForClass('com.example.service.GenericLookupService'), 11, 12)
    }

    void testGetCoverageRatioForClass_EmptyClass() {
        assertRatio(metric.getCoverageRatioForClass('com.example.service.ClientMappingDao'), 0, 0)
    }

    void testGetCoverageRatioForClass_NoSuchClass_ReturnsNull() {
        assert metric.getCoverageRatioForClass('NoSuchClass') == null
    }

}