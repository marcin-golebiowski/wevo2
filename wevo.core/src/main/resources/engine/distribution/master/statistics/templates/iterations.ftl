<h1>Iterations</h1><br/>

<table align=center border=1>
  <tr>
    <th>No.</th>
    <th>Best individual</th>
    <th>Average individual</th>
    <th>Evaluation time</th>
    <th>Additional info</th>
  </tr>
  <#list iterations as iteration>
  <tr>
    <td>${iteration.iteration_number}</td>
    <td>${iteration.best_individual_value}</td>
    <td>${iteration.average_individual_value}</td>
    <td>${iteration.time_spent_in_seconds}</td>
    <td>
      <a href="/statistics?iteration=${iteration.iteration_number}">details</a>
    </td>
  </tr>
  </#list>
</table>
<div class="navigation">
  <#if lastInTable == 0 && firstInTable == 0>
    previous || next
  <#else>
    <#assign leftLast = lastInTable + tableLength>
    <#if (leftLast >= currentIteration)>
      <#assign leftLast = currentIteration - 1>      
    </#if>
    <#assign leftFirst = leftLast - tableLength + 1>
    
    <#assign rightLast = firstInTable - 1>
    <#assign rightFirst = firstInTable - tableLength> 
    <#if (rightFirst < 1)>
      <#assign rightFirst = 1>
    </#if>
    
    <#if lastInTable == currentIteration - 1>
      previous
    <#else>
      <a href="/statistics?iterations=show&last=${leftLast}"
         title="Iterations ${leftLast} - ${leftFirst}">previous</a>      
    </#if>
    ||
    <#if firstInTable == 1>
      next
    <#else>
      <a href="/statistics?iterations=show&last=${rightLast}"
         title="Iterations ${rightLast} - ${rightFirst}">next</a>
    </#if>
  </#if>
</div>