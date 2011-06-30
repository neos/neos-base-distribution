<?php
namespace TYPO3\Demo\Tests\Selenium\Backend\ContentModule;

/*                                                                        *
 * This script belongs to the TYPO3 project.                              *
 *                                                                        *
 * It is free software; you can redistribute it and/or modify it under    *
 * the terms of the GNU General Public License as published by the Free   *
 * Software Foundation, either version 3 of the License, or (at your      *
 * option) any later version.                                             *
 *                                                                        *
 * This script is distributed in the hope that it will be useful, but     *
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHAN-    *
 * TABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General      *
 * Public License for more details.                                       *
 *                                                                        *
 * You should have received a copy of the GNU General Public License      *
 * along with the script.                                                 *
 * If not, see http://www.gnu.org/licenses/gpl.html                       *
 *                                                                        *
 * The TYPO3 project - inspiring people to share!                         *
 *                                                                        */

require_once(__DIR__ . '/AbstractPageSelectorTest.php');

/**
 * Common Backend Selenese functions
 *
 * @license http://www.gnu.org/licenses/gpl.html GNU General Public License, version 3 or later
 */
class MovePageDialogTest extends \TYPO3\Demo\Tests\Selenium\Backend\ContentModule\AbstractPageSelectorTest {

	protected function loginAndGotoMovePageDialog() {
		$this->backendLogin();

		$this->gotoContentFrame();
		$this->clickAndWait('link=Fourth Page');
		$this->gotoOuterFrame();

		$this->activateBreadcrumbMenuPath('menu/main/content/children/movePage');
		$this->assertModuleDialogPresent();
	}

	/**
	 * @test
	 */
	public function movePageDialogHasCurrentPageHighlighted() {
		$this->loginAndGotoMovePageDialog();
		$this->assertTrue($this->isElementPresent('css=' . $this->getModuleDialogCssSelector() . ' .x-grid3-row-selected:contains("Fourth Page")'));
	}

	/**
	 * @test
	 */
	public function movePageDialogCanBeUsedToMovePages() {
		$this->loginAndGotoMovePageDialog();
		$this->assertAtPosition(1, 'Another Page');
		$this->assertAtPosition(2, 'Second Page');
		$this->assertAtPosition(3, 'Third Page');
		$this->assertAtPosition(4, 'Fourth Page');

		$this->moveTo('Fourth Page', 'Second Page');
		$this->triggerPositiveActionInModuleDialog();
		sleep(7); // Time for saving + reloading content frame

		$this->loginAndGotoMovePageDialog();
		$this->assertAtPosition(1, 'Another Page');
		$this->assertAtPosition(2, 'Fourth Page');
		$this->assertAtPosition(3, 'Second Page');
		$this->assertAtPosition(4, 'Third Page');

		// Tear Down: Restore original order
		$this->moveTo('Fourth Page', 'Third Page');
		$this->triggerPositiveActionInModuleDialog();
		sleep(7); // Time for saving + reloading content frame*/
	}

	/**
	 * @test
	 */
	public function whatShouldHappenWhenMovePageIsOpenAndPageIsChanged() {
		$this->markTestIncomplete('what should happen if move page is open and the page is changed?');
	}

}
?>