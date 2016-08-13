<h1>Iteration ${iterationDetailNumber}</h1><br/>

&lt&lt here goes some detailed info about tasks &gt&gt <br/>

<table align=center border=1>
  <tr>
    <th>No.</th>
    <th>Counter name</th>
    <th>Counter value</th>
    <th>Additional info</th>
  </tr>
  <#assign i = 1>
  <#list counters?keys as key>
  <tr>
    <td>${i}</td>
    <td>counter.${key}</td>
    <td>${counters[key]}</td>
    <td>
      <a href="" title= "Show counter values plot">show plot</a>
    </td>
    <#assign i = i + 1>
  </tr>
  </#list>
</table>

