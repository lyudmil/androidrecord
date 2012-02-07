#!/usr/bin/env jruby
require 'yaml'
require 'java'

$CONFIG = YAML.load(File.open(File.dirname(__FILE__) + '/config.yaml'))

require $CONFIG["android-jar-path"]

$CLASSPATH << $CONFIG["output-path"]

java_import com.androidrecord.ActiveRecordBase

print ActiveRecordBase.createSqlFor(eval(ARGV[0]))
exit 0