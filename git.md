
# GIT

## Fork / PR

**Add forked (base) repository with name "_upstream_" to own (forked) repository**

`git remote add upstream https://github.com/ORIGINAL_OWNER/ORIGINAL_REPOSITORY.git`

Example: `git remote add upstream git@github.com:junit-pioneer/junit-pioneer.git`

**Update own `master` branch**

1) Fetch changes from "upstream" (`fetch upstream`)
2) Set own `master` branch equal to `master` branch of the _upstream_ repository.
Discard all changes in own repository.
3) Update own remote ("origin") `master` branch and overwrite all changes.


```
git fetch upstream

git reset --hard upstream/master 

git push origin master -f
```

**Further information**

* [GitHub Helppage](https://help.github.com/en/github/collaborating-with-issues-and-pull-requests/working-with-forks)


