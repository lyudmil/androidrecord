#!/usr/bin/env jruby
require 'yaml'
require 'java'

config = YAML.load(File.open(File.dirname(__FILE__) + '/config.yaml'))

require config["android-jar-path"]

$CLASSPATH << config["output-path"]

java_import com.androidrecord.ActiveRecordBase

print ActiveRecordBase.createSqlFor(eval(ARGV[0]))
exit 0