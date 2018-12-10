

echo ".. downloading releases.html page"
curl -ss https://hadoop.apache.org/releases.html > releases.html

# xmllint --html  ... error !
hadoopVersion=$( xmllint --html releases.html --xpath "string(html/body/div[1]/table/tr[last()]/td[1])" 2> /dev/null )
echo ".. extracted hadoopVersion: ${hadoopVersion}"

downloadSiteUrl=http://apache.crihan.fr/dist/hadoop/common

downloadFileTgz="hadoop-${hadoopVersion}.tar.gz"
if [ ! -e "${downloadFileTgz}" ]
then
	downloadUrl="${downloadSiteUrl}/hadoop-${hadoopVersion}/hadoop-${hadoopVersion}.tar.gz"
	echo ".. downloading ${downloadUrl}"
	curl -ss "${downloadUrl}" -o "${downloadFileTgz}"
else
	echo ".. skip already downloaded ${downloadFileTgz}"
fi

hadoopDir="hadoop-${hadoopVersion}"
if [ ! -e "${hadoopDir}" ]
then
	echo ".. extracting ${hadoopDir}"
	tar zxf "${downloadFileTgz}"
	ln -s "${downloadFileTgz}" hadoop-latest
else
	echo ".. skip already extracted ${hadoopDir}"
fi



