#!/bin/bash
###############################################################################
# run-tests.sh
#
# Test automation script for Sunrise Dental Clinic (Task C).
#
# Single-command build + test cycle:
#   1. Compiles the production source tree (src/) AND the JUnit 5 test tree
#      (test/) together against the JUnit Platform Console Standalone jar,
#      the MariaDB JDBC driver, and the Tomcat 10 servlet-api jar.
#   2. Runs the ENTIRE JUnit test suite (--scan-classpath auto-discovers
#      every @Test in test-classes/, so a newly added test class is picked
#      up automatically -- nothing needs to be registered by hand).
#   3. Points every test at the dedicated sunrise_dental_clinic_test
#      database (-Ddental.db.name=...) so this script can NEVER touch the
#      live/production sunrise_dental_clinic schema, however it is run.
#   4. Prints a clear PASS/FAIL summary and exits non-zero on any failure,
#      so it can be dropped into a CI pipeline unmodified.
#
# Usage:
#   ./run-tests.sh
#
# Requirements (all satisfied by the environment this project is developed
# and marked in):
#   - JDK 17+
#   - MySQL/MariaDB server running locally with the sunrise_dental_clinic_test
#     schema already created (see database/schema.sql, substituting the
#     database name) and the dental_app user granted privileges on it.
#   - lib-test/junit-platform-console-standalone-1.9.1.jar present.
###############################################################################

set -uo pipefail

PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$PROJECT_ROOT"

JUNIT_JAR="lib-test/junit-platform-console-standalone-1.9.1.jar"
MARIADB_JAR="$(find / -name 'mariadb-java-client*.jar' 2>/dev/null | head -1)"
SERVLET_JAR="$(find / -name 'tomcat10-servlet-api*.jar' 2>/dev/null | head -1)"
TEST_CLASSES_DIR="test-classes"
TEST_DB_NAME="sunrise_dental_clinic_test"

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

echo "==============================================================="
echo " Sunrise Dental Clinic -- Automated JUnit Test Run"
echo "==============================================================="

if [ ! -f "$JUNIT_JAR" ]; then
    echo -e "${RED}ERROR:${NC} $JUNIT_JAR not found. Run this script from the project root."
    exit 2
fi
if [ -z "$MARIADB_JAR" ]; then
    echo -e "${RED}ERROR:${NC} mariadb-java-client jar not found on this machine."
    exit 2
fi
if [ -z "$SERVLET_JAR" ]; then
    echo -e "${RED}ERROR:${NC} servlet-api jar not found on this machine (needed to compile the servlet package)."
    exit 2
fi

echo -e "${YELLOW}[1/3]${NC} Compiling src/ + test/ ..."
rm -rf "$TEST_CLASSES_DIR"
mkdir -p "$TEST_CLASSES_DIR"
javac -d "$TEST_CLASSES_DIR" \
    -cp "$JUNIT_JAR:$MARIADB_JAR:$SERVLET_JAR" \
    $(find src -name "*.java") $(find test -name "*.java")

if [ $? -ne 0 ]; then
    echo -e "${RED}BUILD FAILED -- see javac errors above.${NC}"
    exit 1
fi
echo -e "${GREEN}Compiled OK${NC} ($(find "$TEST_CLASSES_DIR" -name '*.class' | wc -l | tr -d ' ') class files)"

echo ""
echo -e "${YELLOW}[2/3]${NC} Running the full JUnit 5 suite against '$TEST_DB_NAME' ..."
echo "        (production database 'sunrise_dental_clinic' is never touched by this script)"
echo ""

java -Ddental.db.name="$TEST_DB_NAME" \
    -jar "$JUNIT_JAR" \
    --class-path "$TEST_CLASSES_DIR:$MARIADB_JAR:$SERVLET_JAR" \
    --scan-classpath "$TEST_CLASSES_DIR" \
    --details=tree \
    --disable-banner \
    2>&1 | grep -v "Picked up\|WARNING: Delegated\|Please use the"

RESULT=${PIPESTATUS[0]}

echo ""
echo -e "${YELLOW}[3/3]${NC} Result"
echo "==============================================================="
if [ "$RESULT" -eq 0 ]; then
    echo -e "${GREEN}ALL TESTS PASSED${NC}"
else
    echo -e "${RED}ONE OR MORE TESTS FAILED${NC} (exit code $RESULT)"
fi
echo "==============================================================="

exit "$RESULT"
