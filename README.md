# Gym Membership Report

Laboratory Work No. 1 for the Cross-Platform Programming course.

## Variant

Variant 20 — Gym.

## Description

The application reads gym membership records from a CSV file, validates them, calculates statistics, and writes a report to the console and an output file.

## Input Format

Each line contains:

client;plan;months;visits;price

## Calculated Statistics
Number of valid records
Average number of visits
Total revenue
Longest membership duration
## Requirements
Java 21
Maven Wrapper
## Build and Verify

Windows:

mvnw.cmd verify
mvnw.cmd package

Linux / macOS:

./mvnw verify
./mvnw package
## Run
java -jar target/lab01-1.0.0.jar

## Custom input and output:

java -jar target/lab01-1.0.0.jar --input data/input.csv --output out/report.txt

## Help:

java -jar target/lab01-1.0.0.jar --help

## Version:

java -jar target/lab01-1.0.0.jar --version
## Testing

The project uses JUnit 5 and SpotBugs.

## GitHub Actions verifies the project on:

Ubuntu
Windows
macOS
