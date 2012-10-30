<?php
namespace TYPO3\Demo\Tests\Selenium\Backend;

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

require_once(__DIR__ . '/../SeleniumTestCase.php');

/**
 * Common Backend Selenese functions
 *
 * @license http://www.gnu.org/licenses/gpl.html GNU General Public License, version 3 or later
 */
abstract class AbstractBackendTestCase extends \TYPO3\Demo\Tests\Selenium\SeleniumTestCase {

	/**
	 * does a backend login, by default with "admin/password".
	 *
	 * @param string $username
	 * @param string $password
	 * @return void
	 * @author Daniel Pötzinger
	 */
	protected function backendLogin($username = 'admin', $password = 'password') {
		$this->open("/typo3/login");
		$this->type("//input[@name='TYPO3[FLOW3][Security][Authentication][Token][UsernamePassword][username]']", $username);
		$this->type("//input[@name='TYPO3[FLOW3][Security][Authentication][Token][UsernamePassword][password]']", $password);
		$this->clickAndWait("//a[@id='typo3-neos-login-button']");
		sleep(4); // some delay to load the backend fully, and to make sure the content frame is loaded.
		// Needed in Chrome at least, and makes tests in Firefox more robust.
	}

	/**
	 * SECTION: BREADCRUMB MENU
	 */

	/**
	 * Activate the given breadcrumb menu path, this includes checking
	 * if it was not active before and that it is active now.
	 *
	 * @param string $menuPath a fully qualified breadcrumb menu path
	 */
	protected function activateBreadcrumbMenuPath($menuPath) {
		$menuPathExpression = '[data-menupath="' . $menuPath .'"]';

		$this->assertBreadcrumbMenuPathShown($menuPath);
		$this->assertBreadcrumbMenuPathNotActivated($menuPath);

		$this->click('css=' . $menuPathExpression);
		sleep(2); // Wait two seconds to let the click take effect.
		$this->assertBreadcrumbMenuPathActivated($menuPath);
	}

	/**
	 * Deactivate the given breadcrumb menu path, this includes checking
	 * if it was active before and that it is not active anymore.
	 *
	 * @param string $menuPath a fully qualified breadcrumb menu path
	 */
	protected function deactivateBreadcrumbMenuPath($menuPath) {
		$menuPathExpression = '[data-menupath="' . $menuPath .'"]';
		$activeMenuPathExpression = '.TYPO3-TYPO3-UserInterface-BreadcrumbMenu-MenuItem-active' . $menuPathExpression;

		$this->assertBreadcrumbMenuPathShown($menuPath);
		$this->assertBreadcrumbMenuPathActivated($menuPath);

		$this->click('css=' . $activeMenuPathExpression);
		sleep(2); // Wait two seconds to let the click take effect.
		$this->assertBreadcrumbMenuPathNotActivated($menuPath);
	}

	/**
	 * Assert that a breadcrumb menu path is visible.
	 *
	 * @param string $menuPath
	 */
	protected function assertBreadcrumbMenuPathShown($menuPath) {
		$menuPathExpression = '[data-menupath="' . $menuPath .'"]';
		$this->assertTrue(
			$this->isVisible('css=' . $menuPathExpression),
			'The menu path ' . $menuPath . 'was not visible, but it should be.'
		);
	}

	/**
	 * Assert that a breadcrumb menu path is not visible or not in DOM.
	 *
	 * @param string $menuPath a fully qualified breadcrumb menu path
	 */
	protected function assertBreadcrumbMenuPathNotShown($menuPath) {
		$menuPathExpression = '[data-menupath="' . $menuPath .'"]';
		if (!$this->isElementPresent('css=' . $menuPathExpression)) {
				// If the menu path is not in the DOM, we are done.
			return;
		}
		$this->assertFalse(
			$this->isVisible('css=' . $menuPathExpression),
			'The menu path ' . $menuPath . 'was visible, but it should not be.'
		);
	}

	/**
	 * Assert that a breadcrumb menu path is activated right now.
	 *
	 * @param string $menuPath a fully qualified breadcrumb menu path
	 */
	protected function assertBreadcrumbMenuPathActivated($menuPath) {
		$menuPathExpression = '[data-menupath="' . $menuPath .'"]';
		$activeMenuPathExpression = '.TYPO3-TYPO3-UserInterface-BreadcrumbMenu-MenuItem-active' . $menuPathExpression;

			// Check that menu path is activated now.
		$this->assertTrue(
			$this->isElementPresent('css=' . $activeMenuPathExpression),
			'The menu path ' . $menuPath . ' is not activated, but it should be.'
		);
	}

	/**
	 * Assert that a breadcrumb menu path is *not activated* right now.
	 * If the breadcrumb menu path is not in the DOM at all, this test will pass.
	 *
	 * @param string $menuPath a fully qualified breadcrumb menu path
	 */
	protected function assertBreadcrumbMenuPathNotActivated($menuPath) {
		$menuPathExpression = '[data-menupath="' . $menuPath .'"]';
		$activeMenuPathExpression = '.TYPO3-TYPO3-UserInterface-BreadcrumbMenu-MenuItem-active' . $menuPathExpression;

			// Check that menu path is activated now.
		$this->assertFalse(
			$this->isElementPresent('css=' . $activeMenuPathExpression),
			'The menu path ' . $menuPath . ' is not found or already activated; but it should not be activated.'
		);
	}

	/**
	 * SECTION: Module Dialog
	 */
	protected function assertModuleDialogPresent() {
		$this->assertTrue(
			$this->isElementPresent('css=.TYPO3-TYPO3-UserInterface-ModuleDialog'),
			'The module dialog for page properties is not shown.'
		);
	}

	protected function getModuleDialogCssSelector() {
		return '.TYPO3-TYPO3-UserInterface-ModuleDialog';
	}

	protected function triggerPositiveActionInModuleDialog() {
		$this->click('css=button.TYPO3-TYPO3-Components-Button-type-positive');
	}

	protected function triggerNegativeActionInModuleDialog() {
		$this->click('css=button.TYPO3-TYPO3-Components-Button-type-negative');
	}

	/**
	 * SECTION: Content Frame
	 */

	/**
	 * Goto inner content frame
	 */
	protected function gotoContentFrame() {
		$this->selectFrame('css=[id="TYPO3.TYPO3.Module.Content.WebsiteContainer"] iframe');
	}

	/**
	 * Go to the outer frame again.
	 */
	protected function gotoOuterFrame() {
		$this->selectFrame('relative=parent');
	}
}
?>