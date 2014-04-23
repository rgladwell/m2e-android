# Contributing to the Android for Maven Eclipse

The **Android for Maven Eclipse** project follows the
[Github Workflow](http://scottchacon.com/2011/08/31/github-flow.html)
and contributions can be made by sending a
[pull requests](https://help.github.com/articles/creating-a-pull-request)
or [raising issues](https://github.com/rgladwell/m2e-android/issues/new).

##Pull requests should...

###...focus on quality

Code should be readable, maintainable,
[clean](http://www.amazon.co.uk/Clean-Code-Handbook-Software-Craftsmanship/dp/0132350882),
follow
[SOLID principals](http://butunclebob.com/ArticleS.UncleBob.PrinciplesOfOod),
not repeat code elsewhere ([DRY](http://c2.com/cgi/wiki?DontRepeatYourself))
and conform to the 
[style guide](https://github.com/rgladwell/m2e-android/blob/master/formatter.xml).

###...be fully testable

Tests should verify new functionality or bugs and be well written with good
coverage. To run the tests in Maven execute the following from within the
cloned project folder:

```
$ mvn verify
```

You can also run the tests inside Eclipse using the PDE JUnit launcher
(`[com.googlecode.eclipse.m2e.android.test] test`) in the test module. There is
also a launcher to run an instance of Eclipse with the latest m2e-android code in
your local workspace (`[com.googlecode.eclipse.m2e.android] run`).

_Note:_ You may find tests can stall because the ADT is waiting for user
interraction from dialogs. To avoid this execute the following command:

```
$ echo 'adtUsed=true' > ~/.android/ddms.cfg
```

###...have a clear intention

Commits should be traceable and grouped according to their intention as
outlined in the
[Government Digital Service Manual](https://www.gov.uk/service-manual/making-software/version-control.html).
Commit messages should be clear and follow the
[standard git format](http://tbaggery.com/2008/04/19/a-note-about-git-commit-messages.html).

##Bug reports should be...

###...Clear

Bug reports should have:

 - Precise, descriptive summaries.
 - Informative, concise descriptions.
 - A neutral tone, avoiding complaints or conjecture.

###...Reproducible

Bug reports should contain:

 - The simplest **steps to reproduce** the issue, or...
 - A failing **test fixture** for the bug.

###...Specific

Only publish **one bug** per report accompanied by:

 - A **detailed description** of the issue focusing on the **facts**.
 - **Expected** and **actual results**.
 - **Versions** of software, platform and operating system used.
 - **Crash data**, including [stack traces](http://i.imgur.com/jacoj.jpg), log
files, screenshots and other relevant information.

###...Unique

Please search for duplicates before reporting a bug and ensure summaries are
include relevant keywords to make it easier for other users to find duplicates.

