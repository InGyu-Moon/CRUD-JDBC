function f_loginClick() {
  var host = document.getElementsByName("host_input")[0].value;
  var port = document.getElementsByName("port_input")[0].value;
  var dbname = document.getElementsByName("dbname_input")[0].value;
  var user = document.getElementsByName("user_input")[0].value;
  var password = document.getElementsByName("password_input")[0].value;

  if (host === '' || port === '' || dbname === '' || user === '' || password === '') {
    alert('Input value is blank!!!');
    return;
  }

  const loginRequest = {
    host: host,
    port: port,
    dbname: dbname,
    user: user,
    password: password
  };

  // Send login request to server
  fetch('/login', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(loginRequest)
  })
  .then(response => response.json())
  .then(data => {
    if (data.status === 'success') {
      alert(data.message);
      // Send request to get table list
      fetch('/getTablelist', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(loginRequest)
      })
      .then(response => response.json())
      .then(data => {
      var str;

        var mainElement = document.querySelector('body');

        str = '<body class="p-3 m-0 border-0 bd-example bd-example-row"><div class="container text-center"><div class="row"><div class="col form-leftTable">Table List<ul class="list-group form-innerTable">';
        for (var i = 0; i < data.tableList.length; i++) {
          str += '<li class="list-group-item">' + data.tableList[i] +'&nbsp;&nbsp;&nbsp;&nbsp;<button value="' + data.tableList[i] + '" type="button" class="btn btn-outline-dark" onclick="f_getTableInfo(this);">View</button></li>';
        }
        str += '</ul></div><div id ="printTableName" class="col">TableInfo<table id = "tableInfo" class="table">';
        str += '</table><button id="whenClick" type="button" class="btn btn-outline-dark" onclick="f_showDataCLick(this) ;">Show Data</button></div></div>';


        //input
            str+= '<br><br><input id="table-name" class="form-control form-control-lg" type="text" placeholder="table name : " style="max-width: 500px;" aria-label=".form-control-lg example">'


        //xlsx파일 db에 업로드
        str += '<br><input style="max-width: 300px;margin-right: 10px;"type="file" id="myFileInput" name="file" class="form-control float-start">';
        str += '<button type="button" class="btn btn-outline-dark float-start" onclick="uploadFile()">Upload</button>'

        //xlsx파일로 출력
        str += '<button id="exportButton" style="margin-left: 10px;" type="button" class="btn btn-outline-dark float-start" onclick="f_export();">Export</button>';


        str += '</div></div></body>';
        mainElement.innerHTML = str;
      })
      .catch(error => console.error(error));
    } else {
      alert(data.message);
    }
  })
  .catch(error => console.error(error));
}

function f_getTableInfo(button){
    //console.log(button.value);
    var tableName = button.value;
    // Send request to server with the selected table name
    fetch('/getTableName', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ tableName: tableName })
    })
    .then(response => response.json())
    .then(data => {
        var targetDiv = document.getElementById('tableInfo');
        var str = '<thead><tr><th scope="col">#</th><th scope="col">Col_Name</th><th scope="col">Col_Type</th><th scope="col">Col_MaxLen</th></tr></thead>';
        for(var i=0;i<data.colList.length;i++){
            str += '<tbody><tr><th scope="row">col_' + i + '</th>';
            str +='<td>' + data.colList[i].colName + '</td>';
            str +='<td>' + data.colList[i].colType + '</td>';
            str +='<td>' + data.colList[i].colMaxLen + '</td></tr>';
        }
        targetDiv.innerHTML = str;
        var targetButton = document.querySelector('#whenClick');
        targetButton.value = data.tableName;
/*
        var printTableName = document.querySelector('#printTableName');
        var str = data.tableName;
        printTableName.innerHTML = str;
        */

    })
    .catch(error => console.error(error));
}
function f_showDataCLick(button){
    var tableName = button.value;
    fetch('/showData', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ tableName: tableName })
    })
    .then(response => response.json())
    .then(data => {
    console.log(data);
        var html = '<html>';
        html += '<head>';
        html += '<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-rbsA2VBKQhggwzxH7pPCaAqO46MgnOM80zW1RWuH61DGLwZJEdK2Kadq2F9CUG65" crossorigin="anonymous">';
        html += '<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/js/bootstrap.bundle.min.js" integrity="sha384-kenU1KFdBIe4zVF0s0G1M5b4hcpxyD9F7jL+jjXkk+Q2h455rYXK/7HAuoJl+0I4" crossorigin="anonymous"></script>';
        html += '</head>';
        html += '<body style=" padding-top: 80px;padding-left: 100px;padding-right: 100px;">';
        html += '<h1 style="padding-bottom: 20px;"><center>Show Data</center></h1>'
        html += '<table class="table text-center">';
        html += '<thead>';
        html += '<tr>';
        html += '<th scope="col">#</th>';
        for (var i = 0; i < data[0].length; i++){
            html += '<th scope="col">' + data[0][i] + '</th>'
        }
        html += '</tr></thead>';
        html += '<tbody>';
        for (var i = 1; i < data.length; i++) {
            html += '<tr>';
            html += '<th scope="row">' + i + '</th>';
            for (var j = 0; j < data[i].length; j++) {
                html += '<td>' + data[i][j] + '</td>';
            }
            html += '</tr>';
        }
        html += '</tbody>';
        html += '</table>';

        html += '</body>';
        html += '</html>';
        var newWindow = window.open();
        newWindow.document.title = 'New Page';
        newWindow.document.write(html);
    })
    .catch(error => console.error(error));
}


//파일 spring으로 전송

function uploadFile() {

const tableName = document.getElementById('table-name').value;

const fileInput = document.getElementById('myFileInput');
  const file = fileInput.files[0];
  const formData = new FormData();
  formData.append('file', file);
  formData.append('tableName', tableName);

  console.log(formData.get('file'));

  fetch('/upload', {
    method: 'POST',
    body: formData
  })
  .then(response => {
    if (!response.ok) {
    alert("upload fail");
      throw new Error('Network response was not ok');
    }
    alert("upload success");
  })
  .then(data => {

  })
  .catch(error => {
    console.error('There was a problem with the fetch operation:', error);
  });
}

function f_export(){
const tableName = document.getElementById('table-name').value;
const formData = new FormData();
formData.append('tableName', tableName);
fetch('/export', {
    method: 'POST',
    body: formData
  })
  .then(response => {
        if (!response.ok) {
          throw new Error('Network response was not ok');
        }
        return response;
      })
        .then(data => {
          alert("export success");
        })
        .catch(error => {
          console.error('There was a problem with the fetch operation:', error);
        });

}
