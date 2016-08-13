<h1>Slaves</h1><br/>

<table align=center border=1>
  <tr>
    <th>No.</th>
    <th>Slave's identifier</th>
    <th>Additional info</th>
  </tr>
  <#assign i = 1>
  <#list slaves as slave>
  <tr>
    <td>${i}</td>
    <td>${slave}</td>
    <td><a href="/statistics?slave=${slave}">details</a></td>
  </tr>
  </#list>
</table>