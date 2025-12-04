#!/bin/bash
case "$(uname)" in
    CYGWIN*)
        CFILE="$(cygpath "$0")"
        RESOLVED_NAME="$(readlink -f "$CFILE")"
        ;;
    Darwin*)
        RESOLVED_NAME="$(readlink "$0")"
        ;;
    OpenBSD)
        RESOLVED_NAME="$(readlink -f "$0")"
        JAVA_HOME="$(/usr/local/bin/javaPathHelper -h keycloak)"
        ;;
    FreeBSD | Linux)
        RESOLVED_NAME="$(readlink -f "$0")"
        ;;
esac

RESOLVED_NAME="${RESOLVED_NAME:-"$0"}"

DIRNAME="$(dirname "$RESOLVED_NAME")"

if [ -z "$JAVA" ]; then
    if [ -n "$JAVA_HOME" ]; then
        JAVA="$JAVA_HOME/bin/java"
    else
        JAVA="java"
    fi
fi

declare -A EXTRA_PROPS
EXTRA_PROPS['kcr.msg.greet_addon']="'with REPL mod'"
EXTRA_OPTS=
for prop in ${!EXTRA_PROPS[@]} ; do
  if [[ $EXTRA_OPTS ]] ; then
    EXTRA_OPTS=$EXTRA_OPTS" -D$prop"="${EXTRA_PROPS[$prop]}"
  else
    EXTRA_OPTS=-D"$prop"="${EXTRA_PROPS[$prop]}"
  fi
done
echo $EXTRA_OPTS
eval exec "$JAVA" "$EXTRA_OPTS" --add-opens=java.base/java.security=ALL-UNNAMED -Dkc.lib.dir=$DIRNAME/client/lib ReplLoader org.keycloak.client.admin.cli.KcAdmMain $DIRNAME/client/keycloak-admin-cli-*.jar
