# AsyncHelper

[![Build Status](https://travis-ci.org/loganathan001/AsyncHelper.svg?branch=master)](https://travis-ci.org/loganathan001/AsyncHelper)
[![Coverage Status](https://coveralls.io/repos/github/loganathan001/AsyncHelper/badge.svg?branch=master)](https://coveralls.io/github/loganathan001/AsyncHelper?branch=master)
[![contributions welcome](https://img.shields.io/badge/contributions-welcome-brightgreen.svg?style=flat)](https://github.com/dwyl/esta/issues)


<a href="https://github.com/loganathan001/AsyncHelper/blob/master/Project/asynchelper/src/main/java/org/vishag/asynchelper/AsyncHelper.java">AsyncHelper</a> is a Java utility to invoke/schedule tasks or fetch data asynchronously using tags/flags in a functional way. This internally utilizes ForkJoin pool to submit the tasks.


Below are the some of the operations that can be perfomed using this utility:
1. Submitting one or more Runnable(s) to run asynchronously.
2. Submitting one or more Supplier(s) to fetch some data asynchronously, which can be then obtained by a tags(key made of one or more Objects)
4. Wait for some flag in one thread until that flag is notified in another thread.
3. Schedule Runnable(s) and Supplier(s) one time or rotating until a flag.
5. Some of the above operations also support option to submit/schedule asynchronously and then wait untill all asynchronous tasks are compete.


Please look into the <a href="https://github.com/loganathan001/AsyncHelper/blob/master/Project/asynchelper/src/test/java/org/vishag/asynchelper/AsyncHelperTest.java">Unit tests</a> for all the use-cases and examples.

To install the latest version, add the below pom dependency entry:
```
<dependency>
  <groupId>org.vishag</groupId>
  <artifactId>async-helper</artifactId>
  <version>1.0.0</version>
</dependency>
```
