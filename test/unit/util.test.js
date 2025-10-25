/**
 * Unit tests for utility functions
 * Using Node.js built-in test runner
 */

import { describe, test } from 'node:test';
import assert from 'node:assert';
import util from '../../src/graphics/util.js';

describe('slope', () => {
    test('returns 0 for horizontal line', () => {
        const result = util.slope(0, 5, 10, 5);
        assert.strictEqual(result, 0);
    });

    test('returns positive slope for ascending line', () => {
        const result = util.slope(0, 0, 10, 10);
        assert.strictEqual(result, 1);
    });

    test('returns negative slope for descending line', () => {
        const result = util.slope(0, 10, 10, 0);
        assert.strictEqual(result, -1);
    });

    test('handles vertical line (returns very large number)', () => {
        const result = util.slope(5, 0, 5, 10);
        assert.ok(Math.abs(result) > 1e9, 'Should return very large number for vertical line');
        assert.ok(result > 0, 'Should be positive for upward vertical line');
    });

    test('handles downward vertical line', () => {
        const result = util.slope(5, 10, 5, 0);
        assert.ok(Math.abs(result) > 1e9, 'Should return very large number for vertical line');
        assert.ok(result < 0, 'Should be negative for downward vertical line');
    });

    test('calculates correct slope for 2:1 ratio', () => {
        const result = util.slope(0, 0, 10, 20);
        assert.strictEqual(result, 2);
    });

    test('calculates correct slope for 1:2 ratio', () => {
        const result = util.slope(0, 0, 20, 10);
        assert.strictEqual(result, 0.5);
    });

    test('handles zero-length line gracefully', () => {
        const result = util.slope(5, 5, 5, 5);
        // Should return a very large number or handle gracefully
        assert.ok(typeof result === 'number');
    });
});

describe('bound', () => {
    test('returns value when within bounds', () => {
        const result = util.bound(5, 0, 10);
        assert.strictEqual(result, 5);
    });

    test('returns min when value is below minimum', () => {
        const result = util.bound(-5, 0, 10);
        assert.strictEqual(result, 0);
    });

    test('returns max when value is above maximum', () => {
        const result = util.bound(15, 0, 10);
        assert.strictEqual(result, 10);
    });

    test('returns min when value equals minimum', () => {
        const result = util.bound(0, 0, 10);
        assert.strictEqual(result, 0);
    });

    test('returns max when value equals maximum', () => {
        const result = util.bound(10, 0, 10);
        assert.strictEqual(result, 10);
    });

    test('handles negative bounds', () => {
        const result = util.bound(-5, -10, -1);
        assert.strictEqual(result, -5);
    });

    test('clamps to negative min', () => {
        const result = util.bound(-15, -10, -1);
        assert.strictEqual(result, -10);
    });

    test('clamps to negative max', () => {
        const result = util.bound(0, -10, -1);
        assert.strictEqual(result, -1);
    });

    test('handles decimal values', () => {
        const result = util.bound(2.5, 0, 5);
        assert.strictEqual(result, 2.5);
    });

    test('handles very small bounds correctly', () => {
        const result = util.bound(0.5, 0.1, 0.9);
        assert.strictEqual(result, 0.5);
    });
});

describe('existing util functions (regression tests)', () => {
    test('distance calculates correctly', () => {
        const result = util.distance(0, 0, 3, 4);
        assert.strictEqual(result, 5);
    });

    test('near returns true for close values', () => {
        const result = util.near(1.0, 1.0001, 0.001);
        assert.strictEqual(result, true);
    });

    test('near returns false for distant values', () => {
        const result = util.near(1.0, 1.1, 0.001);
        assert.strictEqual(result, false);
    });

    test('direction calculates correctly for horizontal right', () => {
        const result = util.direction(0, 0, 10, 0);
        assert.ok(Math.abs(result - 0) < 0.0001);
    });

    test('direction calculates correctly for vertical up', () => {
        const result = util.direction(0, 0, 0, 10);
        assert.ok(Math.abs(result - Math.PI/2) < 0.0001);
    });

    test('tenthStr formats to one decimal place', () => {
        const result = util.tenthStr(3.14159);
        assert.strictEqual(result, '3.1');
    });
});
