5.0.0 / 2018-01-03
==================

* Remove tasks that are better handled elsewhere
    * Untar can be handled with built in `tarfile`
    * Unzip can be handled with built in `zipfile`
    * Download there are 2 or 3 better plugins in the wild

3.1.0 / 2016-3-18
===================

* Switch Download task to httpclient, so 302s are followed

2.2.0 / 2015-1-30
===================

* Move to gradle 2.2.1

1.12.1 / 2014-06-11
===================

* Upgrade of nebula-plugin-plugin to 1.12.1

1.9.1 / 2014-01-14
=================

* Add AlternativeArchiveTask which can be extended instead of AbstractArchiveTask while still giving the same signatures

1.9.0 / 2014-01-10
=================

* Initial release
