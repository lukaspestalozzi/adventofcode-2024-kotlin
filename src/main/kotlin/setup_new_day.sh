
COPY_FROM=$1
DAY=$2

echo "copy $COPY_FROM to $DAY"

cp "$COPY_FROM".kt ./Day"$DAY".kt
touch Day"$DAY"_test.txt
touch Day"$DAY".txt
