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
class CreatePageDialogTest extends \TYPO3\Demo\Tests\Selenium\Backend\ContentModule\AbstractPageSelectorTest {

	protected function loginAndGotoCreatePageDialog() {
		$this->backendLogin();

		sleep(3);
		$this->gotoContentFrame();
		$this->clickAndWait('link=Second Page');
		$this->gotoOuterFrame();

		$this->activateBreadcrumbMenuPath('menu/main/content/children/createPage');
		$this->assertModuleDialogPresent();
	}

	/**
	 * @test
	 */
	public function pagePositionSelectorIsLoaded() {
		$this->loginAndGotoCreatePageDialog();

		$this->assertAtPosition(1, 'Add new page here');
		$this->assertAtPosition(2, 'First Subpage');
		$this->assertAtPosition(3, 'Second Subpage');
		$this->assertAtPosition(4, 'Third Subpage');
	}

	/**
	 * @test
	 */
	public function pageIsNotCreatedIfNodeNameContainsInvalidCharacters() {
		$this->loginAndGotoCreatePageDialog();

		$this->type('css=' . $this->getModuleDialogCssSelector() . ' form input[name="nodeName"]', 'aNodeNameWithUppercaseCharacters');
		$this->type('css=' . $this->getModuleDialogCssSelector() . ' form input[name="properties.title"]', 'My new Page');

		$this->triggerPositiveActionInModuleDialog();
		sleep(2);
		$this->assertModuleDialogPresent(); // The module dialog must still be present, as the node name has errors.
	}

	/**
	 * @test
	 */
	public function pageIsCreatedAtSpecifiedPositionAndCanBeDeletedAgain() {
		$this->loginAndGotoCreatePageDialog();

		$nodeName = uniqid('mynewpage');
		$this->moveTo('Add new page here', 'Second Subpage');
		$this->type('css=' . $this->getModuleDialogCssSelector() . ' form input[name="nodeName"]', $nodeName);
		$this->type('css=' . $this->getModuleDialogCssSelector() . ' form input[name="properties.title"]', 'My new Page');

		$this->triggerPositiveActionInModuleDialog();
		sleep(5); // Wait for page creation and content refresh

			// Assertion
		$this->gotoContentFrame();
		$this->assertTextPresent('Start typing here');
		$this->clickAndWait('link=Second Page'); // Go one level up again
		$this->assertTrue($this->isElementPresent('css=li.current ul li:nth-child(1):contains("First Subpage")'), 'First subpage not on correct position');
		$this->assertTrue($this->isElementPresent('css=li.current ul li:nth-child(2):contains("Second Subpage")'), 'Second subpage not on correct position');
		$this->assertTrue($this->isElementPresent('css=li.current ul li:nth-child(3):contains("My new Page")'), 'the new page is not on correct position');
		$this->assertTrue($this->isElementPresent('css=li.current ul li:nth-child(4):contains("Third Subpage")'), 'Third subpage not on correct position');

			// Tear Down: Delete page again
		$this->clickAndWait('link=My new Page');
		$this->gotoOuterFrame();
		$this->activateBreadcrumbMenuPath('menu/main/content/children/deletePage');
		$this->triggerNegativeActionInModuleDialog();
		sleep(2);
	}

	/**
	 * @test
	 */
	public function whatShouldHappenWhenCreatePageIsOpenAndPageIsChanged() {
		$this->markTestIncomplete('what should happen if create page is open and the page is changed?');
	}

}
?>