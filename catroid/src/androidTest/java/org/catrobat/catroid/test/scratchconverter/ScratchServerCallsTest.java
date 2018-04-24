/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * An additional term exception under section 7 of the GNU Affero
 * General Public License, version 3, is available at
 * http://developer.catrobat.org/license_additional_term
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.test.scratchconverter;

import android.test.InstrumentationTestCase;

import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.ScratchProgramData;
import org.catrobat.catroid.common.ScratchSearchResult;
import org.catrobat.catroid.web.ServerCalls;
import org.catrobat.catroid.web.WebScratchProgramException;
import org.catrobat.catroid.web.WebconnectionException;

import java.io.InterruptedIOException;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

public class ScratchServerCallsTest extends InstrumentationTestCase {

	public ScratchServerCallsTest() {
		super();
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		ServerCalls.useTestUrl = true;
	}

	@Override
	protected void tearDown() throws Exception {
		ServerCalls.useTestUrl = false;
		super.tearDown();
	}

	public void testScratchSearchWithEmptyQueryParam() throws WebconnectionException, InterruptedIOException {
		ScratchSearchResult searchResult = ServerCalls.getInstance().scratchSearch("", 20, 0);
		List<ScratchProgramData> programDataList = searchResult.getProgramDataList();

		assertNotNull(programDataList);
		assertEquals(0, programDataList.size());
		assertNotNull(searchResult);
		assertEquals(0, searchResult.getPageNumber());
	}

	private void checkScratchProgramData(ScratchProgramData programData) {
		assertThat(programData.getId(), is(greaterThan(0L)));
		assertThat(programData.getTitle(), not(isEmptyOrNullString()));

		assertThat(programData.getOwner(), not(isEmptyOrNullString()));

		assertNotNull(programData.getNotesAndCredits());
		assertNotNull(programData.getInstructions());

		assertNotNull(programData.getImage());
		assertNotNull(programData.getImage().getUrl());

		assertEquals(Constants.SCRATCH_IMAGE_DEFAULT_WIDTH, programData.getImage().getWidth());
		assertEquals(Constants.SCRATCH_IMAGE_DEFAULT_HEIGHT, programData.getImage().getHeight());

		assertNotNull(programData.getModifiedDate());
		assertNotNull(programData.getSharedDate());

		assertThat(programData.getViews(), is(greaterThanOrEqualTo(0)));
		assertThat(programData.getLoves(), is(greaterThanOrEqualTo(0)));
		assertThat(programData.getFavorites(), is(greaterThanOrEqualTo(0)));
	}

	public void testScratchSearchWithQueryParam() throws WebconnectionException, InterruptedIOException {
		ScratchSearchResult searchResult = ServerCalls.getInstance().scratchSearch("test", 20, 0);
		List<ScratchProgramData> programDataList = searchResult.getProgramDataList();

		assertNotNull(searchResult);
		assertNotNull(programDataList);
		assertThat(programDataList.size(), is(greaterThan(0)));
		assertEquals(0, searchResult.getPageNumber());
		assertThat(searchResult.getProgramDataList().size(), is(greaterThan(0)));
		assertThat(searchResult.getProgramDataList().size(), is(lessThanOrEqualTo(20)));

		for (ScratchProgramData programData : programDataList) {
			checkScratchProgramData(programData);
		}
	}

	public void testScratchSearchMaxNumberOfItemsParam() throws WebconnectionException, InterruptedIOException {
		final int maxNumberOfItems = 10;

		ScratchSearchResult searchResult = ServerCalls.getInstance().scratchSearch("test", maxNumberOfItems, 0);
		List<ScratchProgramData> programDataList = searchResult.getProgramDataList();

		assertNotNull(searchResult);
		assertNotNull(programDataList);
		assertThat(programDataList.size(), is(greaterThan(0)));
		assertEquals(0, searchResult.getPageNumber());
		assertThat(searchResult.getProgramDataList().size(), is(greaterThan(0)));

		assertThat(searchResult.getProgramDataList().size(), is(lessThanOrEqualTo(maxNumberOfItems)));

		for (ScratchProgramData programData : programDataList) {
			checkScratchProgramData(programData);
		}
	}

	public void testScratchSearchPagination() throws WebconnectionException, InterruptedIOException {
		for (int pageIndex = 1; pageIndex < 3; pageIndex++) {
			ScratchSearchResult searchResult = ServerCalls.getInstance().scratchSearch("test", 20, pageIndex);
			List<ScratchProgramData> programDataList = searchResult.getProgramDataList();

			assertNotNull(searchResult);
			assertNotNull(programDataList);
			assertThat(programDataList.size(), is(greaterThan(0)));
			assertEquals(pageIndex, searchResult.getPageNumber());
			assertThat(searchResult.getProgramDataList().size(), is(greaterThan(0)));
			assertThat(searchResult.getProgramDataList().size(), is(lessThanOrEqualTo(20)));

			for (ScratchProgramData programData : programDataList) {
				checkScratchProgramData(programData);
			}
		}
	}

	public void testFetchDefaultScratchPrograms() throws InterruptedIOException, WebconnectionException {
		ScratchSearchResult searchResult = ServerCalls.getInstance().fetchDefaultScratchPrograms();
		List<ScratchProgramData> programDataList = searchResult.getProgramDataList();

		assertNotNull(searchResult);
		assertNotNull(programDataList);
		assertThat(programDataList.size(), is(greaterThan(0)));
		assertEquals(0, searchResult.getPageNumber());
		assertThat(searchResult.getProgramDataList().size(), is(greaterThan(0)));

		for (ScratchProgramData programData : programDataList) {
			checkScratchProgramData(programData);
		}
	}

	public void testFetchScratchProgramDetails() throws
			WebconnectionException,
			WebScratchProgramException,
			InterruptedIOException {

		long expectedProgramID = 10205819;
		String expectedProgramTitle = "Dancin' in the Castle";
		String expectedProgramOwner = "jschombs";
		ScratchProgramData programData = ServerCalls.getInstance().fetchScratchProgramDetails(expectedProgramID);

		checkScratchProgramData(programData);
		assertEquals(programData.getId(), expectedProgramID);
		assertEquals(programData.getTitle(), expectedProgramTitle);
		assertEquals(programData.getOwner(), expectedProgramOwner);
	}
}
