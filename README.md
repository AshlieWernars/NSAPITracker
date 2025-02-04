# NS Train Tracker

## Overview

The NS Train Tracker is a 24/7 capable service that utilizes the [NS Developer API](https://apiportal.ns.nl/) to monitor and log real-time train data.
It tracks material numbers, journeys, arrivals, departures, and disruptions, logging all this to files for safekeeping and archiving.

## Features

**Tracks Active MaterialNumbers:**
- Logs all active journeys for each material number every 15 minutes.
- Monitors and logs arrivals and departures for the following stations:
  - Almere Centrum (alm)
  - Amsterdam Centraal (asd)
  - Den Haag Centraal (gvc)
  - Zwolle (zl)
  - Schiphol Airport (shl)
  - Leiden Centraal (ledn)
  - Leeuwarden (lwn)
- **Journey Combination:**
  - Combines arrivals and departures into journeys when applicable.
- **Tracks all active disruptions** and keeps track of the following:
  - Affected stations
  - Reason for disruption
  - Expected resolution time
  - When disruptions are resolved, they are written to a CSV file.

## Requirements
- Java 17 or later
- [JSON.jar](https://repo1.maven.org/maven2/org/json/json/20250107/json-20250107-javadoc.jar)
