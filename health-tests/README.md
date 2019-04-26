# Common Health Tests

This module provides abstract / stub test classes and helper classes for writing integration tests
that verify the health configuration of a service.

Each service that uses `health` should implement the following tests:

## AbstractAdminEndpointsTest

Example implementation:

    public class TestAdminEndpointsTest extends AbstractAdminEndpointsTest {
    }

## AbstractRootEndpointTest

Example implementation:

    public class TestRootEndpointTest extends AbstractRootEndpointTest {
    }