<?php
declare(ENCODING = 'utf-8');
namespace F3\Demo\Tests\Selenium\Backend;
require_once dirname(__FILE__).'/BackendTestCase.php';


/*                                                                        *
 * This script is part of the TYPO3 project - inspiring people to share!  *
 *                                                                        *
 * TYPO3 is free software; you can redistribute it and/or modify it under *
 * the terms of the GNU General Public License version 2 as published by  *
 * the Free Software Foundation.                                          *
 *                                                                        *
 * This script is distributed in the hope that it will be useful, but     *
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHAN-    *
 * TABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General      *
 * Public License for more details.                                       *
 *                                                                        */


/**
 * Verify that login to backend works like expected for the Demopackage
 *
 * 
 * @license http://www.gnu.org/licenses/lgpl.html GNU Lesser General Public License, version 3 or later
 */
class LoginTest extends BackendTestCase {
   
	

    /**
     * if the login-in to the backend fails
     * @test
     * @author Daniel Pötzinger
     */
    public function loginWithWrongCredentialsFails() {   
    	//$this->resetCookiesAndOpenStartPage(); 	
        $this->backendLogin("username","unknownpassword");              
        $this->checkText("Wrong username or password");
    }
    
	/**
     * if the login-in to the backend is ok
     * @test
     * @author Daniel Pötzinger
     */
    public function loginWithCorrectCredentialsWorks() {   
    	//$this->resetCookiesAndOpenStartPage(); 	
        $this->backendLogin("username","realpassword");              
        $this->checkText("Wrong username or password");
    }
    
  
}
?>