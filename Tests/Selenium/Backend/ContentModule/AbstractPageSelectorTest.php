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
 * Tests for the Breadcrumb Menu
 *
 * @license http://www.gnu.org/licenses/gpl.html GNU General Public License, version 3 or later
 */
abstract class AbstractPageSelectorTest extends \TYPO3\Demo\Tests\Selenium\Backend\AbstractBackendTestCase {
	protected function moveTo($titleOfElementToBeMoved, $titleOfAnchor) {
		$elementToBeMoved = 'css=' . $this->getModuleDialogCssSelector() . ' .x-grid3-row:contains("' . $titleOfElementToBeMoved . '")';
		$anchor = 'css=' . $this->getModuleDialogCssSelector() . ' .x-grid3-row:contains("' . $titleOfAnchor . '")';

		$this->mouseDownAt($elementToBeMoved, '3,3');
		sleep(1);
		$this->mouseMoveAt($elementToBeMoved, '4,4');
		sleep(1);
		$this->mouseMoveAt($anchor, '3,3');
		sleep(1);
		$this->mouseUpAt($anchor, '3,3');
	}

	protected function assertAtPosition($position, $title) {
		$this->assertTrue($this->isElementPresent('css=' . $this->getModuleDialogCssSelector() . ' .x-grid3-row:nth-child(' . $position . ') :contains("' . $title . '")'),
			'At position ' . $position . ', the element "' . $title . '" was not found.');
	}
}
?>