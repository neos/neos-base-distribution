<?php
declare(ENCODING = 'utf-8');

/*                                                                        *
 * This script belongs to the FLOW3 build system.                         *
 *                                                                        *
 * It is free software; you can redistribute it and/or modify it under    *
 * the terms of the GNU Lesser General Public License as published by the *
 * Free Software Foundation, either version 3 of the License, or (at your *
 * option) any later version.                                             *
 *                                                                        *
 * This script is distributed in the hope that it will be useful, but     *
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHAN-    *
 * TABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser       *
 * General Public License for more details.                               *
 *                                                                        *
 * You should have received a copy of the GNU Lesser General Public       *
 * License along with the script.                                         *
 * If not, see http://www.gnu.org/licenses/lgpl.html                      *
 *                                                                        *
 * The TYPO3 project - inspiring people to share!                         *
 *                                                                        */

require_once('phing/Task.php');
require_once('PHP/UML.php');

/**
 * PHP_UML task for Phing
 *
 * @license http://www.gnu.org/licenses/lgpl.html GNU Lesser General Public License, version 3 or later
 */
class PhpUmlTask extends Task {

	/**
	 * @var string
	 */
	protected $input;

	/**
	 * @var string
	 */
	protected $output;

	/**
	 * @var string
	 */
	protected $title;

	/**
	 * @param string $path
	 * @return void
	 */
	public function setInput($path) {
		$this->input = $path;
	}

	/**
	 * @param string $output
	 * @return void
	 */
	public function setOutput($output) {
		$this->output = $output;
	}

	/**
	 * @param string $title
	 * @return void
	 */
	public function setTitle($title) {
		$this->title = $title;
	}

	/**
	 * @return void
	 * @author Karsten Dambekalns <karsten@typo3.org>
	 */
	public function main() {
		$this->log('Calling PHP_UML on ' . $this->input);
		$renderer = new PHP_UML();
		$renderer->deploymentView = FALSE;
		$renderer->onlyApi = TRUE;
		$renderer->setInput($this->input);
		$renderer->parse($this->title);
		$renderer->generateXMI(2.1, 'utf-8');
		$renderer->export('html', $this->output);
	}

}

?>