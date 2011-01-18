/**
 * Creating your test runners
 */

- Copy the Runner templates you want to use from the Templates folder
  to <Package>/Tests/JavaScript/

- Include the JavaScript libraries you need to test, or are required
  by the classes you want to test

- Include the JavaScript files containing the actual tests

- For the JUnitRunner: change the <PackageName> marker to your package name

/**
 * Run your tests
 */

- Using your browser
  If you want to run your WebRunner, open it from the local filesystem using your browser.
  URL example: file:///<pathToFLOW3>/Packages/Application/<Package>/Tests/JavaScript/WebRunner.html

- From the command line
  execute the following commands:
  cd /<pathToFLOW3>/Build/Common/JSUnit/Jasmine/test
  ./envjs.runner.sh /<pathToFLOW3>/Packages/Application/<Package>/Tests/JavaScript/<Runner.html>