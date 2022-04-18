# Building a release

* Check that every version has a license. You can do this with `testScripts/checkLicense.sh
* Update the version numbers in all the `pom.xml` files. You can use `testScripts/checkVersion.sh` to check whether you are done.
* At the root directory, run `mvn clean install`.
* Check that `counting-lanugage-impl/target/run.sh` and `counting-lanugage-impl/target/run.bat` can be used to run counting-language.
* Commit your code.
* Using `git tag -a -m <some message> v<version>`, tag the commit with the released version.
* Upload the release to GitHub:
  * The executable .jar.
  * The source code as `.zip`.
  * The source code as `.tar.gz`.
  * `run.sh`.
  * `run.bat`.
* Update all the `pom.xml` files to the next snapshot. Use `testScripts/checkVersion.sh` to check this step.
* Commit your code.
* Push your code.
* Push your tag.
