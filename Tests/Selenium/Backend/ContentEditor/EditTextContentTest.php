<?php
namespace TYPO3\Demo\Tests\Selenium\Backend\ContentEditor;

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
 * Tests for the Breadcrumb Menu
 *
 * @license http://www.gnu.org/licenses/gpl.html GNU General Public License, version 3 or later
 */
class EditTextContentText extends \TYPO3\Demo\Tests\Selenium\Backend\AbstractBackendTestCase {
	/**
	 * @test
	 */
	public function doubleClickInContentAreaSwitchesToEditModeAndDoubleEscapeExitsTheEditMode() {
		$this->backendLogin();

			// Activate edit mode
		$this->gotoContentFrame();
		$this->doubleClick('css=.typo3-neos-editable:contains("defaced")');
		sleep(1);
		$this->gotoOuterFrame();
		$this->assertBreadcrumbMenuPathActivated('menu/main/content/children/edit');

			// Deactivate edit mode
		$this->gotoContentFrame();
		sleep(1);
		$this->keyDown('css=body', '\27');
		$this->keyDown('css=body', '\27');
		sleep(2);
		$this->gotoOuterFrame();
		$this->assertBreadcrumbMenuPathNotActivated('menu/main/content/children/edit');
	}
}
?>