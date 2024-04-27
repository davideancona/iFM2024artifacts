#!/usr/bin/env bash


# specify monitor alias path for modules imported by the spec (like deep_subdict)
swipl -O -p monitor=/home/davide/git/RMLatDIBRIS/monitor /home/davide/git/RMLatDIBRIS/monitor/monitor.pl -- "$@"
