/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2017 The Catrobat Team
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

package org.catrobat.catroid.uiespresso.content.brick;

import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.bricks.ChangeYByNBrick;
import org.catrobat.catroid.content.bricks.LoopEndBrick;
import org.catrobat.catroid.content.bricks.RepeatBrick;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.uiespresso.util.BaseActivityInstrumentationRule;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.action.ViewActions.swipeUp;

import static org.catrobat.catroid.uiespresso.content.brick.BrickTestUtils.checkIfBrickAtPositionShowsString;
import static org.catrobat.catroid.uiespresso.content.brick.BrickTestUtils.deleteBrickAtPosition;
import static org.catrobat.catroid.uiespresso.content.brick.BrickTestUtils.onScriptList;

@RunWith(AndroidJUnit4.class)
public class LoopBrickTest {

	@Rule
	public BaseActivityInstrumentationRule<ScriptActivity> baseActivityTestRule = new
			BaseActivityInstrumentationRule<>(ScriptActivity.class, true, false);

	@Before
	public void setUp() throws Exception {

		RepeatBrick repeatBrick = new RepeatBrick(3);
		LoopEndBrick endBrick = new LoopEndBrick(repeatBrick);

		Script script = BrickTestUtils.createProjectAndGetStartScript("LoopBrickTest1");

		script.addBrick(repeatBrick);
		script.addBrick(new ChangeYByNBrick(-10));
		script.addBrick(endBrick);


		baseActivityTestRule.launchActivity(null);
	}

	@Test
	public void testRepeatBrick() {
		int repeatBrickPosition = 1;
		int changeYByNBrickPosition = 2;
		int endBrickPosition = 3;

		checkIfBrickAtPositionShowsString(0, R.string.brick_when_started);
		checkIfBrickAtPositionShowsString(repeatBrickPosition, R.string.brick_repeat);
		checkIfBrickAtPositionShowsString(changeYByNBrickPosition, R.string.brick_change_y_by);
		checkIfBrickAtPositionShowsString(endBrickPosition, R.string.brick_loop_end);

		/*deleteBrickAtPosition(endBrickPosition);
		changeYByNBrickPosition = 1;
		checkIfBrickAtPositionShowsString(changeYByNBrickPosition, R.string.brick_change_y_by);*/

		onScriptList().atPosition(3).perform(swipeUp());
	}
}
