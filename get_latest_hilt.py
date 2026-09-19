import urllib.request
import json
import ssl

ctx = ssl.create_default_context()
ctx.check_hostname = False
ctx.verify_mode = ssl.CERT_NONE

url = "https://search.maven.org/solrsearch/select?q=g:com.google.dagger+AND+a:hilt-android&rows=5&wt=json"
req = urllib.request.Request(url, headers={'User-Agent': 'Mozilla/5.0'})
try:
    with urllib.request.urlopen(req, context=ctx) as response:
        data = json.loads(response.read().decode())
        docs = data['response']['docs']
        for doc in docs:
            print(f"Latest version of {doc['a']}: {doc['latestVersion']}")
except Exception as e:
    print(f"Failed: {e}")
