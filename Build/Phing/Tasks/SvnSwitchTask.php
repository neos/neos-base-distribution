<?php
declare(ENCODING = 'utf-8');

/*                                                                        *
 * This script belongs to the FLOW3 project.                              *
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

require_once('phing/tasks/ext/svn/SvnBaseTask.php');

/**
 * svn switch task for Phing
 *
 * @version $Id$
 * @license http://www.gnu.org/licenses/lgpl.html GNU Lesser General Public License, version 3 or later
 */
class SvnSwitchTask extends SvnBaseTask {

	/**
	 * @var string
	 */
	protected $targetUrl;

	/**
	 * @var string
	 */
	protected $dir;

	/**
	 * @param string $targetUrl
	 * @return void
	 */
	public function setTargetUrl($targetUrl) {
		$this->targetUrl = $targetUrl;
	}

	/**
	 * @param string $dir
	 * @return void
	 */
	public function setDir($dir) {
		$this->dir = $dir;
	}

	/**
	 * @return void
	 * @author Karsten Dambekalns <karsten@typo3.org>
	 */
	public function main() {
		$this->setup('switch');
		$this->run(array($this->targetUrl, $this->dir));
	}

}

?>