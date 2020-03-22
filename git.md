
# GIT

## Fork / PR

**Fork als Repo mit Namen "upstream" hinzufügen**

`git remote add upstream https://github.com/ORIGINAL_OWNER/ORIGINAL_REPOSITORY.git`

Beispiel: `git remote add upstream git@github.com:junit-pioneer/junit-pioneer.git`

**Eigenen "master" aktualisieren**

1) Änderungen im "upstream"-Repo holen (`fetch upstream`)
2) Eigenen master auf master des "upstream"-Repos setzen und alle Änderungen verwerfen
3) Eigenen master im eigenen "origin" aktualisieren und alle Änderungen verwerfen

```
git fetch upstream

git reset --hard upstream/master 

git push origin master -f
```


