#!/bin/bash
# Tests parts b) SOAP and c) REST against data/googlescholar.xml
SOAP=http://localhost:8080/soap/ResearchersWS
REST=http://localhost:8080/rest/api/publications
PASS=0
FAIL=0

check() {
  if [ "$2" = "$3" ]; then
    echo "PASS  $1  ($3)"
    PASS=$((PASS+1))
  else
    echo "FAIL  $1  (expected $2, got $3)"
    FAIL=$((FAIL+1))
  fi
}

soap() {
  curl -s -X POST "$SOAP" -H "Content-Type: text/xml;charset=UTF-8" \
    -d "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ws=\"http://ws/\"><soapenv:Body>$1</soapenv:Body></soapenv:Envelope>"
}
count_soap()  { soap "$1" | grep -o '<return>' | wc -l | tr -d ' '; }
fault_soap()  { soap "$1" | grep -c 'Fault' | tr -d ' '; }
count_rest()  { curl -s -H "Accept: application/json" "$REST$1" | grep -o '"title"' | wc -l | tr -d ' '; }
rest_status() { curl -s -o /dev/null -w "%{http_code}" "$REST$1"; }
status()      { curl -s -o /dev/null -w "%{http_code}" "$1"; }

echo "===== b) SOAP – Researchers ====="
check "WSDL is published"                     200 "$(status "$SOAP?wsdl")"
check "getAllResearchers"                     21  "$(count_soap '<ws:getAllResearchers/>')"
check "getResearcherByName Marco Vieira"      1   "$(count_soap '<ws:getResearcherByName><name>Marco Vieira</name></ws:getResearcherByName>')"
check "getResearcherByName (lower case)"      1   "$(count_soap '<ws:getResearcherByName><name>marco vieira</name></ws:getResearcherByName>')"
check "getResearcherByName unknown"           0   "$(count_soap '<ws:getResearcherByName><name>Nobody</name></ws:getResearcherByName>')"
check "getResearchersByInterest Software Security"    3 "$(count_soap '<ws:getResearchersByInterest><interest>Software Security</interest></ws:getResearchersByInterest>')"
check "getResearchersByInterest software engineering" 5 "$(count_soap '<ws:getResearchersByInterest><interest>software engineering</interest></ws:getResearchersByInterest>')"
check "getResearchersByInterest Machine Learning"     3 "$(count_soap '<ws:getResearchersByInterest><interest>Machine Learning</interest></ws:getResearchersByInterest>')"
check "getResearcherByName without name -> no fault"          0 "$(fault_soap '<ws:getResearcherByName/>')"
check "getResearchersByInterest without interest -> no fault" 0 "$(fault_soap '<ws:getResearchersByInterest/>')"

echo "===== c) REST – Publications ====="
check "GET all"                               17  "$(count_rest '')"
check "GET by title"                          1   "$(count_rest '/title?title=Reflecting%20on%20self-adaptive%20software%20systems')"
check "GET by title unknown -> 404"           404 "$(rest_status '/title?title=Nobody')"
check "GET by researcher Marco Vieira"        4   "$(count_rest '/researcher?name=Marco%20Vieira')"
check "GET by researcher Nuno Laranjeiro"     11  "$(count_rest '/researcher?name=Nuno%20Laranjeiro')"
check "GET by researcher unknown"             0   "$(count_rest '/researcher?name=Nobody')"
check "GET by researcher without name -> 400" 400 "$(rest_status '/researcher?name=')"
check "GET by publisher IEEE"                 8   "$(count_rest '/venue?name=IEEE')"
check "GET by publisher Elsevier"             6   "$(count_rest '/venue?name=Elsevier')"
check "GET by publisher Springer"             1   "$(count_rest '/venue?name=Springer')"
check "GET by publisher + researcher IEEE / Nuno Laranjeiro" 7 "$(count_rest '/venue-researcher?venue=IEEE&researcher=Nuno%20Laranjeiro')"
check "GET by publisher + researcher ACM / Marco Vieira"     2 "$(count_rest '/venue-researcher?venue=ACM&researcher=Marco%20Vieira')"
check "GET venue-researcher missing researcher -> 400"     400 "$(rest_status '/venue-researcher?venue=IEEE')"
check "Search title 'rest'"                   2   "$(count_rest '/search?title=rest')"
check "Search title 'web service'"            5   "$(count_rest '/search?title=web%20service')"

echo "================================="
echo "$PASS passed, $FAIL failed"
