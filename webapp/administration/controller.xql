xquery version "1.0";

import module namespace app = "http://exist-db.org/application" at "application.xqm";
import module namespace pckg = "http://exist-db.org/packages" at "packages/module.xqm";

let $action := request:get-parameter("action",())
let $firstRun := pckg:firstRunImport()
let $baseURL : = concat($exist:context, $exist:root, $exist:controller, '/')
let $URL : = concat($exist:root, $exist:controller, '/')
return 
if ($action eq 'logout') then
	<dispatch xmlns="http://exist.sourceforge.net/NS/exist">
		{session:invalidate()}
		<redirect url="{$baseURL}"/>
	</dispatch>

else if ($exist:path eq 'login.xql') then
	<ignore xmlns="http://exist.sourceforge.net/NS/exist">
		<cache-control cache="yes"/>
	</ignore>
else if (not (xmldb:is-authenticated())) then
	if ($exist:path eq '') then
		<dispatch xmlns="http://exist.sourceforge.net/NS/exist">
			<redirect url="{$baseURL}"/>
		</dispatch>
	else
		<dispatch xmlns="http://exist.sourceforge.net/NS/exist">
			<forward url="{concat($URL, "login.xql")}"/>
		</dispatch>

else if ($exist:path eq '/') then
	<dispatch xmlns="http://exist.sourceforge.net/NS/exist">
		<forward url="index.xql"/>
	</dispatch>

else if (starts-with($exist:path, '/error/')) then
	<ignore xmlns="http://exist.sourceforge.net/NS/exist">
		<cache-control cache="yes"/>
	</ignore>
else
	let $app := $exist:resource
	let $parent := substring(request:get-uri(), string-length($baseURL))
	let $parent := substring($parent, 0, string-length($parent) - string-length($app))
	return
	if (ends-with($app, (".xql",".js",".css",".png",".ico",".gif"))) then
		<ignore xmlns="http://exist.sourceforge.net/NS/exist">
			<cache-control cache="yes"/>
		</ignore>
	else if (not($app eq "")) then
		<dispatch xmlns="http://exist.sourceforge.net/NS/exist">
			<redirect url="{concat($baseURL, app:primaryFile($parent, $app, "aea0e743-f7eb-400c-a0f0-61d8436ca59e"))}"/>
		</dispatch>
	else
		() (: TODO: restrict :)