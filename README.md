# Description

The resources API is a low level api that manages access to singular file-like entities or filesystem-like hierarchies.
Resources must be accessible through URIs that pinpoint their location and optionally a principal to manage authentication/authorization.

# Releasing backend resources

A resource hierarchy may contain state that needs to be properly handled (e.g. an ftp connection).
However there are two things to consider:

- If you have an FTP connection for an entire hierarchy, you don't want to close it because one resource in it was closed
- Resources are mainly used to manage input/output to them which has to be managed separately, we'd rather not end up with code like:

```java
public void doSomething(ResourceContainer container, String name) {
	ReadableResource resource = container.getChild(name);
	try {
		ReadableContainer<ByteBuffer> readable = resource.getReadable();
		try {
			...
		}
		finally {
			readable.close();
		}
	}
	finally {
		resource.close();
	}
}
```

To this end the ResourceRoot interface was added which implements Closeable. 
This means the onerous logic to manage overall state of backend resources was delegated to the root of the hierarchy.
Only the one who actually looks up a resource through its URI will have to close it, passing along that resource (or child resources) does not force everyone to manage them.
As with streams the best practice is that the one who originally resolved the resource, closes it.

# Ignore rules

The system uses an ignore syntax compatible with .gitignore.
It will pickup .gitignore files and you can also set an `.ignore file.

## 1. Comments

Lines starting with a hash (`#`) are treated as comments and are ignored.

```gitignore
# This is a comment
# Ignore all log files
*.log
```

## 2. Specific Files

Simply list the filename. This ignores that file *anywhere* in the repository.

```gitignore
# Ignores 'debug.log' in the root, in 'src/', in 'src/api/v1/', etc.
debug.log
```

## Anchoring (Root Directory)

Start a pattern with a forward slash (`/`) to match files or directories **only in the root** of the repository (where the `.gitignore` file is).

```gitignore
# Only ignores 'config.ini' in the root directory
/config.ini

# Won't ignore 'src/config.ini'
```

-----

## 4. Directories

To ignore an entire directory, add a trailing slash (`/`). This ignores that directory *anywhere* it appears.

```gitignore
# Ignores any directory named 'node_modules'
node_modules/

# Ignores any directory named 'build'
build/
```

**Note:** You can combine this with anchoring: `/build/` ignores *only* the `build` directory in the root.

## 5. Wildcards (`*` and `**`)

### `*` (Asterisk)

The asterisk (`*`) matches zero or more characters (but not a slash `/`).

```gitignore
# Ignores all files ending with .log
*.log

# Ignores all files starting with 'temp'
temp*
```

### `**` (Double Asterisk)

The double asterisk (`**`) matches zero or more *directories* (it can match across slashes).

```gitignore
# Ignores any 'notes.txt' file in any subdirectory
**/notes.txt

# Ignores all files inside any 'java' directory
**/java/*
```

## 6. Negation (Exceptions)

Start a pattern with an exclamation mark (`!`) to create an exception to a previous rule.

```gitignore
# Ignore all .txt files
*.txt

# BUT do not ignore 'important.txt'
!important.txt
```

**Important Caveat:** You **cannot** re-include a file if its parent directory has been ignored.

```gitignore
# This will NOT work
build/
!build/keep-this.exe
```

Because the `build/` directory itself is ignored, we won't even look inside it to see the exception.

## 7. Other Glob Patterns (`?` and `[]`)

These are less common but follow standard "glob" syntax:

* `?`: Matches exactly one character.
* `[]`: Matches one character from a set.

```gitignore
# Matches 'data1.log', 'data2.log', but not 'data10.log'
data?.log

# Matches 'data1.log' and 'data2.log'
data[1-2].log
```

## 8. Escaping

If you need to ignore a file that literally contains a special character (like `#` or `*`), you can escape it with a backslash (`\`).

```gitignore
# Ignores the file named '#config.txt'
\#config.txt
```