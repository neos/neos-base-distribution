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

require_once(__DIR__ . '/../AbstractBackendTestCase.php');

/**
 * Common Backend Selenese functions
 *
 * @license http://www.gnu.org/licenses/gpl.html GNU General Public License, version 3 or later
 */
class PagePropertiesDialogTest extends \TYPO3\Demo\Tests\Selenium\Backend\AbstractBackendTestCase {

	/**
	 * @test
	 */
	public function pagePropertiesDialogContainsPageTitle() {
		$this->backendLogin();
		$this->assertPagePropertiesDialogPageTitle('^Home$');
	}

	/**
	 * @test
	 */
	public function pagePropertiesDialogContainsPageTitleAfterPageHasChanged() {
		$this->backendLogin();
		$this->gotoContentFrame();
		$this->clickAndWait('link=Another Page');
		$this->gotoOuterFrame();
		$this->assertPagePropertiesDialogPageTitle('^Another Page$');
	}

	/**
	 * Helper which checks that the page properties dialog contains the page title $title.
	 *
	 * @param string $title
	 */
	protected function assertPagePropertiesDialogPageTitle($title) {
		$this->activateBreadcrumbMenuPath('menu/main/content/children/edit');
		$this->activateBreadcrumbMenuPath('menu/main/content/children/edit/children/pageProperties');

		$this->assertModuleDialogPresent();

		$this->verifyValue('css=' . $this->getModuleDialogCssSelector() . ' form input[name=properties.title]', $title);
	}

	/**
	 * @test
	 */
	public function pagePropertiesDialogTitleCanBeChanged() {
		$this->backendLogin();
		$this->assertPagePropertiesDialogPageTitle('Home');
		$this->type('css=' . $this->getModuleDialogCssSelector() . ' form input[name=properties.title]', 'NewTitleOfHomepage');

			// Submit
		$this->triggerPositiveActionInModuleDialog();
		sleep(5);
		$this->deactivateBreadcrumbMenuPath('menu/main/content/children/edit');

		$this->gotoContentFrame();
		$this->verifyTextPresent('NewTitleOfHomepage');
		$this->gotoOuterFrame();
		$this->assertPagePropertiesDialogPageTitle('^NewTitleOfHomepage$');

			// TearDown: change title back
		$this->type('css=' . $this->getModuleDialogCssSelector() . ' form input[name=properties.title]', 'Home');
		$this->triggerPositiveActionInModuleDialog();
	}

	/**
	 * @test
	 */
	public function whatShouldHappenWhenPagePropertiesAreOpenAndPageIsChanged() {
		$this->markTestIncomplete('what should happen if the page properties are open and the page is changed?');
	}

}
?>