Execute Once Workflow Operation
-------------------------------

This operation handler runs a single command with multiple MediaPackage elements as arguments. The command
may be used to create a new mediapackage element, or to add configuration properties to the running workflow.

To run a command for each element in a MediaPackage, use the [Execute Many](execute-many-woh.md) operation.

Commands run by this operation handler must first be included in the `commands.allowed` list in the
[Execute Service](../modules/execute.md#service-configuration) configuration.

### Parameter table

All parameters are empty by default if not specified. The special parameters `#id`, `#flavor` and `#out` are described
in [Execute Service: Parameter Substitution](../modules/execute.md#parameter-substitution)

|Configuration keys|Example    |Description       |Required?|
|------------------|-----------|------------------|---------|
|exec              |qtfaststart|The command to run|Yes      |
|params            |-f -t 15 <nobr>#{flavor(presentation/distribute)}</nobr> #{out}|The arguments to the command. This string allows some placeholders for input and output MediaPackage elements (see Parameter Substitution)|Yes|
|load              |1.5|A floating point estimate of the load imposed on the node by this job|No|
|set-workflow-properties|true / false|Import workflow properties from the output file|No
|output-filename   |outfile.mp4|Specifies the name of the file created by the command (if any), without path information. Used as the last part of the #{out} parameter|No|
|expected-type     |Track|Specifies the type of MediaPackage element produced by the command: Manifest, Timeline, Track, Catalog, Attachment, Publication, Other|Required if output- filename is present|
|target-flavor     |presentation/processed|Specifies the flavor of the resulting Mediapackage element created by the command. If no new element is created, this parameter is ignored.|Required if output- filename is present|
|target-tags       |execservice, -trim|List of tags that will be applied to the resulting Mediapackage element. Tags starting with "-" will be deleted from the element instead, if present. The resulting element may be the same as the input element.|No|

If `set-workflow-properties` is true, the command should write a plain-text properties file to the location specified by
`#{out}` in the key-value format supported by the [Java
Properties](http://docs.oracle.com/javase/8/docs/api/java/util/Properties.html#load-java.io.Reader-) class, for example:

````
key1=value1
key2=value2
````

### Operation Examples

Run a command which combines two tracks into a new track:

````
<operation
  id="execute-once"
  fail-on-error="true"
  exception-handler-workflow="error"
  description="Run command">
  <configurations>
    <configuration key="exec">ges-launch</configuration>
    <configuration key="params">-e #{flavor(presenter/source)} 0 5m14s #{flavor(presentation/source)} 0 14s</configuration>
    <configuration key="output-filename">result.avi</configuration>
    <configuration key="target-flavor">output/joined</configuration>
    <configuration key="target-tags">joined, -tojoin</configuration>
    <configuration key="expected-type">Track</configuration>
  </configurations>
</operation>
````

Run a command which inspects a mediapackage and adds new configuration properties to the running workflow, leaving the
mediapackage unchanged:

````
<operation
  id="execute-once"
  fail-on-error="true"
  exception-handler-workflow="error"
  description="Inspect media and update workflow properties">
  <configurations>
    <configuration key="exec">/usr/local/bin/oc-inspect.sh</configuration>
    <configuration key="params">#{out} #{id}</configuration>
    <configuration key="set-workflow-properties">true</configuration>
    <configuration key="output-filename">wf.properties</configuration>
    <configuration key="expected-type">Attachment</configuration>
  </configurations>
</operation>
````
