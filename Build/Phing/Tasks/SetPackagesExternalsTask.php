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
require_once('phing/BuildException.php');

/**
 * Task for Phing to set svn:externals in FLOW3 distribution
 *
 * @version $Id$
 * @license http://www.gnu.org/licenses/lgpl.html GNU Lesser General Public License, version 3 or later
 */
class SetPackagesExternalsTask extends SvnBaseTask {

	/**
	 * @var string
	 */
	protected $packagesPath;

	/**
	 * @var boolean
	 */
	protected $fixRevision;

	/**
	 * @param string $packagesPath
	 * @return void
	 */
	public function setPackagesPath($packagesPath) {
		$this->packagesPath = $packagesPath;
	}

	/**
	 * @param boolean $fixRevision
	 * @return void
	 */
	public function setFixRevision($fixRevision) {
		$this->fixRevision = $fixRevision;
	}

	/**
	 * @return void
	 * @author Robert Lemke <robert@typo3.org>
	 */
	public function main() {
		$latestPackageRevisions = array();

		if ($this->fixRevision) {
			foreach (new DirectoryIterator($this->packagesPath) as $file) {
				$filename = $file->getFilename();
				if ($file->isDir() && $filename[0] !== '.') {
					$this->setup('info');
					$xml = simplexml_load_string($this->run(array($file->getPathName(), '--xml')));
					if ($xml === FALSE) {
						throw new BuildException('svn info returned no parseable xml.');
					}
					$latestPackageRevisions[$filename] = (integer)$xml->entry->commit['revision'];
				}
			}
		}

		$this->setup('propget');
		$existingExternals = $this->run(array('svn:externals', $this->packagesPath));
		$newExternals = '';
		foreach (explode(chr(10), $existingExternals) as $line) {
			if (strlen(trim($line)) > 0) {
				$line = preg_replace('/\-r[0-9]+/', '', $line);
				$line = preg_replace('/ +/', ' ', $line);
				list($packageKey, $uri) = explode(' ', $line);

				$newExternals .=
					str_pad($packageKey, 50) .
					($this->fixRevision ? str_pad(('-r' . $latestPackageRevisions[$packageKey]), 10) : '') .
					trim($uri) . chr(10);
			}
		}
		$this->setup('propset');
		$existingExternals = $this->run(array('svn:externals', escapeshellarg($newExternals), $this->packagesPath));
	}

}

?>