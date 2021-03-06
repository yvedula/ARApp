var beginsTime;
var serverURL = "http://www.clipartbest.com/cliparts/4c9/aLK/"
//var serverURL = "http://www.clipartbest.com/cliparts/4c9/aLK/"
function $_GET(param) {
	var vars = {};
	window.location.href.replace( location.hash, '' ).replace(
		/[?&]+([^=&]+)=?([^&]*)?/gi, // regexp
		function( m, key, value ) { // callback
			vars[key] = value !== undefined ? value : '';
		}
	);

	if ( param ) {
		return vars[param] ? vars[param] : null;
	}
	return vars;
}

var ar_id = $_GET('arid');
var poi_id = $_GET('poiid');
var imageName = $_GET('file');

var World = {
	loaded: false,

	init: function initFn() {
		this.createOverlays();
	},

	/*******createOverlays: function createOverlaysFn() {
		/*
			First an AR.ClientTracker needs to be created in order to start the recognition engine. It is initialized with a URL specific to the target collection. Optional parameters are passed as object in the last argument. In this case a callback function for the onLoaded trigger is set. Once the tracker is fully loaded the function worldLoaded() is called.

			Important: If you replace the tracker file with your own, make sure to change the target name accordingly.
			Use a specific target name to respond only to a certain target or use a wildcard to respond to any or a certain group of targets.

		this.tracker = new AR.ClientTracker("assets/magazine.wtc", {
			onLoaded: this.worldLoaded
		});******/

	createOverlays: function createOverlaysFn() {
		/*
			First an AR.ClientTracker needs to be created in order to start the recognition engine. It is initialized with a URL specific to the target collection. Optional parameters are passed as object in the last argument. In this case a callback function for the onLoaded trigger is set. Once the tracker is fully loaded the function worldLoaded() is called.

			Important: If you replace the tracker file with your own, make sure to change the target name accordingly.
			Use a specific target name to respond only to a certain target or use a wildcard to respond to any or a certain group of targets.
		*/
//		this.tracker = new AR.ClientTracker("assets/surfer.wtc", {
//			onLoaded: this.worldLoaded
//		});
		this.tracker = new AR.ClientTracker("file:///storage/emulated/0/ar_images/tracker.wtc", {
			onLoaded: this.worldLoaded
		});
		/*
			The next step is to create the augmentation. In this example an image resource is created and passed to the AR.ImageDrawable. A drawable is a visual component that can be connected to an IR target (AR.Trackable2DObject) or a geolocated object (AR.GeoObject). The AR.ImageDrawable is initialized by the image and its size. Optional parameters allow for position it relative to the recognized target.
		*/

		/* Create overlay for page one */
        var imgOne = new AR.ImageResource("file:///storage/emulated/0/ar_images/"+imageName);

		var overlayOne = new AR.ImageDrawable(imgOne, 3, {
			offsetX: -0.15,
			offsetY: 0
		});

		/*
			The last line combines everything by creating an AR.Trackable2DObject with the previously created tracker, the name of the image target and the drawable that should augment the recognized image.
			Please note that in this case the target name is a wildcard. Wildcards can be used to respond to any target defined in the target collection. If you want to respond to a certain target only for a particular AR.Trackable2DObject simply provide the target name as specified in the target collection.
		*/
		var pageOne = new AR.Trackable2DObject(this.tracker, "*", {
			drawables: {
				cam: overlayOne
			}
		});
	},

	worldLoaded: function worldLoadedFn() {
		var completedTime = Date.now();
		var loadTime = completedTime - beginsTime;
		console.log('Render complete at: '+completedTime);
		console.log('Time taken: '+loadTime);
		var cssDivLeft = " style='display: table-cell;vertical-align: middle; text-align: right; width: 50%; padding-right: 15px;'";
		var cssDivRight = " style='display: table-cell;vertical-align: middle; text-align: left;'";
		document.getElementById('loadingMessage').innerHTML =
			"<div" + cssDivLeft + ">Scan Target &#35;1 (surfer):</div>" +
//			"<div" + cssDivRight + "><img src='assets/surfer.png'></img></div>";
"<div" + cssDivRight + "><img src='file:///storage/emulated/0/ar_images/'"+imageName+"></img></div>";


		// Remove Scan target message after 10 sec.
		setTimeout(function() {
			var e = document.getElementById('loadingMessage');
			e.parentElement.removeChild(e);
		}, 10000);
	}
};

World.init();
