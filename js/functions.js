function findColors(URL,onSuccess,onFailure)
{
	 $.ajax({
	 	type: 'GET',
	    url: "GetColors",
		data:{"URL":URL},
	    crossOrigin:true,
	    crossDomain: true,
	    headers: {
                    'Access-Control-Allow-Origin': '*'
                },
	    success: function(responseData, textStatus, jqXHR) {
	        if(responseData.success)
	        {
	        	onSuccess(responseData.content);
	        	
	        }
	        else
	        {
	        	onFailure(responseData.message);
	        }
	    },
	    dataType:'json',
	    error: function (responseData, textStatus, errorThrown) {
	       	onFailure(responseData.message);
	        
	    }
     });
}

function startLoader(containerId)
{
	x="";
	// var x='<div class="ui segment">';
	x+='<div class="ui active dimmer">';
	x+='<div class="ui massive text loader">Loading</div>';
	x+='</div><p></p>';
	// x+='</div>';
	$("#"+containerId).html(x);
}


function onlyUnique(value, index, self) { 
    return self.indexOf(value) === index;
}

function handleClick(checkbox)
{
	var ch=checkbox.checked;
	var id="#div"+String(checkbox.id);
	if(ch)
	{

		$(id).show();
	}
	else
	{
		$(id).hide();	
	}
}



function fillColors(containerId,items)
{
	containerId="#"+containerId;
	$(containerId).html("");
	$(containerId).append("Click blocks to copy their color code to clipboard<br/><br/>");
	$(containerId).addClass("blurBG");
	$(containerId).append("Selected : <input type='text' id='copyTarget' readonly><br/><br/>");
	var colorsMap= [];
	
	var allColors = [];

	for(j=0;j<items.length;j++)
	{
		if(items[j].colors.length>0)
		{
			colorsMap[items[j].url]=items[j].colors;			
			// Array.prototype.push.apply(allColors, items[j].colors);
			// allColors = allColors.concat(items[j].colors);
			var htmlText= "<input checked=true type='checkbox' id='wrap"+j+"' onclick='handleClick(this)'>&nbsp;&nbsp;"+items[j].url+"<br/><br/>";
			$(containerId).append(htmlText);
			var wrap = $("<div>",{"id":"divwrap"+j});
			allColors = items[j].colors;
			for(i=0;i<allColors.length;i++)
			{
				var $div = $("<button>", {"class": "colorBoxSmall"});
				$div.css("background",allColors[i]);
				$div.attr("data-toogle","tooltip");
				// $div.html(data[i]);
				$div.attr("title",allColors[i]);
				$div.click(function(){
				$("#copyTarget").val($(this).attr("title"));
				$("#copyTarget").select();
				try {
					var successful = document.execCommand('copy');
					var msg = successful ? 'successful' : 'unsuccessful';
					console.log('Copying text command was ' + msg);
				} 
				catch (err) {
					console.log('Oops, unable to copy');
				}
				});
				wrap.append($div);
			}

			$(containerId).append(wrap);		
			$(containerId).append("<br/><br/>");	

		}


	}





}

function stopLoader(containerId)
{
	$("#"+containerId).html("");

}

