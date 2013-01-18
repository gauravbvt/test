#!/bin/bash

patch -p0 <connect.diff
 
echo '*** Note:  you will need to run "svn revert -R ." to get a workable WM Studio...'
