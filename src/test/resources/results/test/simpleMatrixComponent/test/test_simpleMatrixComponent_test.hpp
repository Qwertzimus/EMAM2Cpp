#ifndef TEST_SIMPLEMATRIXCOMPONENT_TEST
#define TEST_SIMPLEMATRIXCOMPONENT_TEST

#include "catch.hpp"
#include "../test_simpleMatrixComponent.h"

TEST_CASE("test.SimpleMatrixComponentTest1", "[test_simpleMatrixComponent]") {
    test_simpleMatrixComponent component;
    component.init();
            component.status = 1.0;
        component.execute();
            component.status = 2.0;
        component.execute();
            component.status = 3.0;
        component.execute();
}

#endif

