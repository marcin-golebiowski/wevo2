<html>
  <head>
    <meta http-equiv="content-type" content="text/html;charset=UTF-8" />
    <title>WEvo2: evolution statistics</title>
    <style>
      /* <![CDATA[ */
      body {
        font-family: verdana, arial, helvetica, sans-serif;
        font-size: 12px;
        line-height: 18px;
        background-color: #C8F3C6;
      }
      table {
        font-family: verdana, arial, helvetica, sans-serif;
        font-size: 12px;
        color: #333333;
        border-width: 1px;
        border-color: #666666;
        border-collapse: collapse;
      }
      table th {
        border-width: 1px;
        padding: 8px;
        border-style: solid;
        border-color: #666666;
        background-color: #66AB5A;
      }
      table td {
        border-width: 1px;
        padding: 8px;
        border-style: solid;
        border-color: #666666;
        background-color: #C8F3C6;
      }
      a { 
        font-family: verdana, arial, helvetica, sans-serif;
        font-size: 12px;
        text-decoration: none;
        color: #000000; 
      }
      a:visited { 
        font-family: verdana, arial, helvetica, sans-serif;
        font-size: 12px;
        text-decoration: none;
        color: #000000; 
      }
      a:hover { 
        font-family: verdana, arial, helvetica, sans-serif;
        font-size: 12px;
        text-decoration: underline;
        color: blue;
      }
      div {
        font-family: verdana, arial, helvetica, sans-serif;
        font-size: 12px;      
      }
      div.box {
        margin: auto;
        border: 1px solid black;
        padding: 0px;
        width: 800px;
        min-height: 600px;
        background: #73D66E;
      }
      div.header {
        margin: auto;
        border-top: none;
        border-right: none;
        border-bottom: 1px solid black;
        border-left: none;
        padding-top: 30px;
        width: 800px;
        height: 50px;
        font-size: 30px;
        font-weight: bold;
        text-align: center;
      }
      div.bar {
        margin: auto;
        border-top: none;
        border-right: none;
        border-bottom: 1px solid black;
        border-left: none;
        padding: 0px;
        width: 800px;
        height: 20px;
        text-align: right;
      }
      div.pageCenter {
        margin: auto;
        border: none;
        padding: 0px;
        width: 800px;
        min-height: 150px;
      }
      div.menu {
        margin: auto;
        border: none;
        text-align: center;
        padding: 0px;
        width: 150px;
        min-height: 150px;
        float: left;
      }
      div.frame {
        margin: auto;
        border: none;
        padding: 0px;
        width: 650px;
        min-height: 485px;
        text-align: right;
        float: right;
      }
      div.footer {
        height: 20px;
        border-top: 1px solid black;
        color: black;
        text-align: center;
        clear:both;
      }
      div.navigation {
        width: 650px;
        height: 30px;
        padding-top: 10px;
        color: black;
        text-align: center;
      }      
      /* ]]> */    
    </style>    
  </head>
  <body>
    <div class="box">
      <div class="header">
        WEvo2: evolution statistics
      </div>
      <div class="bar">
        Iteration number ${currentIteration}.
        Evolution start-time: ${startTime}.
        Total running time: ${runningTime} sec.
      </div>
      <div class="pageCenter">
        <div class="menu">
          <br/>
          <a href="/statistics?iterations=show">Iterations</a><br/>
          <a href="/statistics?slaves=show">Slaves</a><br/>
          <a href="/statistics?settings=show">Settings</a><br/>
          <br/>
          <a href="/statistics">Main page</a>
        </div>
        <div class="frame">
          ${frame}
        </div>
        <div class="footer">
          WEvo2 distributed evolutionary library
        </div>
      </div>
    </div>
  </body>
</html>