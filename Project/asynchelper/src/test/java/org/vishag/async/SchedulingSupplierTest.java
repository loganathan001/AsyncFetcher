/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at

 *   http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.vishag.async;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * The class SchedulingSupplierTest.
 * 
 * @author Loganathan.S &lt;https://github.com/loganathan001&gt;
 */
@RunWith(Parameterized.class)
public final class SchedulingSupplierTest {

	/** The watcher. */
	@Rule
	public TestRule watcher = new TestWatcherAndLogger();
	
	/** The scheduling supplier. */
	private SchedulingSupplier schedulingSupplier;
	
	/**
	 * Inputs.
	 *
	 * @return the collection
	 */
	@Parameters
	public static Collection<Object[]> inputs() {
		return Arrays.asList(new Object[][] {
				{SchedulingSupplier.getDefault()},
				{SchedulingSupplier.of(Executors
						.newScheduledThreadPool(10))	},
				{SchedulingSupplier.of(Executors
						.newScheduledThreadPool(10), AsyncContext.newInstance())	}
			});
	}
	
	 /**
 	 * Instantiates a new scheduling supplier test.
 	 *
 	 * @param schedulingSupplier the scheduling supplier
 	 * @throws Exception the exception
 	 */
 	public SchedulingSupplierTest(SchedulingSupplier schedulingSupplier) throws Exception {
		 this.schedulingSupplier = schedulingSupplier;
	 }
	
	

	/**
	 * Test schedule multiple supplier for single access.
	 *
	 * @throws InterruptedException
	 *             the interrupted exception
	 */
	@Test
	public void testScheduleMultipleSupplierForSingleAccess() throws InterruptedException {
		AtomicInteger count = new AtomicInteger(0);
		boolean suppliers = schedulingSupplier.scheduleSupplierForSingleAccess(10, 100, TimeUnit.MILLISECONDS, true,
				() -> {
					int index = count.getAndIncrement();
					return (index + 1) * 10;
				}, 3, "Scheduled", "Multiple", "Suppliers", "key");

		assertTrue(suppliers);

		List<Integer> retVals = schedulingSupplier
				.waitAndGetFromSuppliers(Integer.class, "Scheduled", "Multiple", "Suppliers", "key")
				.collect(Collectors.toList());
		assertEquals(retVals.size(), 3);
		AtomicInteger val = new AtomicInteger(0);
		retVals.forEach(value -> assertEquals(val.addAndGet(10), (int) value));

	}

	/**
	 * Test schedule single supplier until flag.
	 *
	 * @throws InterruptedException
	 *             the interrupted exception
	 */
	@Test
	public void testScheduleSingleSupplierUntilFlag() throws InterruptedException {
		int[] retVal = new int[1];
		schedulingSupplier.scheduleSupplierUntilFlag(10, 100, TimeUnit.MILLISECONDS, true,
				"TestSingleSuppliersUntilFlag", () -> {
					return retVal[0]++;
				});

		Thread.sleep(1000);

		List<Integer> result = schedulingSupplier.notifyAndGetForFlag(Integer.class, "TestSingleSuppliersUntilFlag")
				.collect(Collectors.toList());
		assertTrue(result.size() > 0);
		for (int i = 0; i < result.size(); i++) {
			assertTrue(i == result.get(i));
		}
		TestUtil.print("" + result);

	}

	/**
	 * Test schedule supplier.
	 *
	 * @throws InterruptedException
	 *             the interrupted exception
	 */
	@Test
	public void testScheduleSupplier() throws InterruptedException {
		AtomicInteger count = new AtomicInteger(0);
		Supplier<Integer>[] scheduleSuppliers = schedulingSupplier.scheduleSupplier(10, 100, TimeUnit.MILLISECONDS,
				true, () -> {
					TestUtil.printTime();
					int index = count.getAndIncrement();
					return (index + 1) * 10;
				}, 3);

		assertEquals(scheduleSuppliers.length, 3);
		AtomicInteger val = new AtomicInteger(0);
		Stream.of(scheduleSuppliers).map(Supplier::get).forEach(value -> assertEquals(val.addAndGet(10), (int) value));
	}

	/**
	 * Test schedule supplier and wait.
	 *
	 * @throws InterruptedException
	 *             the interrupted exception
	 */
	@Test
	public void testScheduleSupplierAndWait() throws InterruptedException {
		AtomicInteger count = new AtomicInteger(0);
		List<Integer> retVals = schedulingSupplier.scheduleSupplierAndWait(0, 100, TimeUnit.MILLISECONDS, true, () -> {
			TestUtil.printTime();
			int index = count.getAndIncrement();
			return (index + 1) * 10;
		}, 3).collect(Collectors.toList());
		assertEquals(retVals.size(), 3);
		AtomicInteger val = new AtomicInteger(0);
		retVals.forEach(value -> assertEquals(val.addAndGet(10), (int) value));
	}

	/**
	 * Test schedule supplier and wait single time.
	 *
	 * @throws InterruptedException
	 *             the interrupted exception
	 */
	@Test
	public void testScheduleSupplierAndWaitSingleTime() throws InterruptedException {
		Optional<Integer> retVal = schedulingSupplier.scheduleSupplierAndWait(0, TimeUnit.SECONDS, () -> {
			return 10;
		});
		assertEquals((int) retVal.get(), 10);
	}

	/**
	 * Test schedule supplier for single access single time.
	 *
	 * @throws InterruptedException
	 *             the interrupted exception
	 */
	@Test
	public void testScheduleSupplierForSingleAccessSingleTime() throws InterruptedException {
		boolean suppliers = schedulingSupplier.scheduleSupplierForSingleAccess(10, TimeUnit.MILLISECONDS, () -> {
			return 10;
		}, "Scheduled", "Single", "Supplier", "key");

		assertTrue(suppliers);

		Optional<Integer> result = schedulingSupplier.waitAndGetFromSupplier(Integer.class, "Scheduled", "Single",
				"Supplier", "key");
		assertTrue(result.isPresent());
		assertEquals((int) result.get(), 10);

	}

	/**
	 * Test schedule suppliers.
	 *
	 * @throws InterruptedException
	 *             the interrupted exception
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testScheduleSuppliers() throws InterruptedException {
		Supplier<Integer>[] scheduleSuppliers = schedulingSupplier.scheduleSuppliers(10, 100, TimeUnit.MILLISECONDS,
				true, () -> {
					return 10;
				}, () -> {
					return 20;
				}, () -> {
					return 30;
				}, () -> {
					return 40;
				}, () -> {
					return 50;
				});

		assertEquals(scheduleSuppliers.length, 5);
		AtomicInteger val = new AtomicInteger(0);
		Stream.of(scheduleSuppliers).map(Supplier::get).forEach(value -> assertEquals(val.addAndGet(10), (int) value));
	}

	/**
	 * Test schedule suppliers and wait.
	 *
	 * @throws InterruptedException
	 *             the interrupted exception
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testScheduleSuppliersAndWait() throws InterruptedException {
		List<Integer> retVals = schedulingSupplier.scheduleSuppliersAndWait(0, 300, TimeUnit.MILLISECONDS, true, () -> {
			return 10;
		}, () -> {
			return 20;
		}, () -> {
			return 30;
		}, () -> {
			return 40;
		}, () -> {
			return 50;
		}).collect(Collectors.toList());

		assertEquals(retVals.size(), 5);
		AtomicInteger val = new AtomicInteger(0);
		retVals.forEach(value -> assertEquals(val.addAndGet(10), (int) value));
	}

	/**
	 * Test schedule suppliers for single access.
	 *
	 * @throws InterruptedException
	 *             the interrupted exception
	 */
	@Test
	public void testScheduleSuppliersForSingleAccess() throws InterruptedException {
		@SuppressWarnings("unchecked")
		boolean suppliers = schedulingSupplier.scheduleSuppliersForSingleAccess(10, 100, TimeUnit.MILLISECONDS, true,
				new Supplier[] { () -> {
					return 10;
				}, () -> {
					return 20;
				}, () -> {
					return 30;
				}, () -> {
					return 40;
				}, () -> {
					return 50;
				} }, "Scheduled", "Multiple", "Suppliers", "key");

		assertTrue(suppliers);

		List<Integer> retVals = schedulingSupplier
				.waitAndGetFromSuppliers(Integer.class, "Scheduled", "Multiple", "Suppliers", "key")
				.collect(Collectors.toList());
		assertEquals(retVals.size(), 5);
		AtomicInteger val = new AtomicInteger(0);
		retVals.forEach(value -> assertEquals(val.addAndGet(10), (int) value));

	}

	/**
	 * Test schedule supplier single time.
	 *
	 * @throws InterruptedException
	 *             the interrupted exception
	 */
	@Test
	public void testScheduleSupplierSingleTime() throws InterruptedException {
		Supplier<Integer> scheduleSupplier = schedulingSupplier.scheduleSupplier(10, TimeUnit.MILLISECONDS, () -> {
			return 10;
		});

		assertEquals((int) scheduleSupplier.get(), 10);
	}

	/**
	 * Test schedule suppliers until flag.
	 *
	 * @throws InterruptedException
	 *             the interrupted exception
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testScheduleSuppliersUntilFlag() throws InterruptedException {
		schedulingSupplier.scheduleSuppliersUntilFlag(10, 100, TimeUnit.MILLISECONDS, true, "TestSuppliersUntilFlag",
				() -> {
					return 0;
				}, () -> {
					return 1;
				}, () -> {
					return 2;
				}, () -> {
					return 3;
				}, () -> {
					return 4;
				});

		Thread.sleep(1000);

		List<Integer> result = schedulingSupplier.notifyAndGetForFlag(Integer.class, "TestSuppliersUntilFlag")
				.collect(Collectors.toList());
		assertTrue(result.size() > 0);
		int val = 0;
		for (int i = 0; i < result.size(); i++) {
			assertTrue(val == result.get(i));
			if (val < 5 - 1) {
				val++;
			} else {
				val = 0;
			}
		}
		TestUtil.print("" + result);

	}
	
	/**
	 * Test close.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void testClose() throws Exception {
		SchedulingSupplier schedulingSupplier = SchedulingSupplier.of(Executors.newScheduledThreadPool(5));
		Optional<String> resultSupplier = schedulingSupplier.scheduleSupplierAndWait(1, TimeUnit.MILLISECONDS, () -> "Test");
		schedulingSupplier.close();
		assertEquals(resultSupplier.get(), "Test");
		schedulingSupplier.close();
	}
	
	/**
	 * Test close with exception.
	 *
	 * @throws Exception the exception
	 */
	@Test (expected=Exception.class)
	public void testCloseWithException() throws Exception {
		SchedulingSupplier schedulingSupplier = SchedulingSupplier.of(Executors.newScheduledThreadPool(5));
		Optional<String> resultSupplier = schedulingSupplier.scheduleSupplierAndWait(1, TimeUnit.MILLISECONDS, () -> "Test1");
		schedulingSupplier.close();
		assertEquals(resultSupplier.get(), "Test1");
		
		schedulingSupplier.scheduleSupplierAndWait(1, TimeUnit.MILLISECONDS, () -> "Test1");
		fail();
	}
}
