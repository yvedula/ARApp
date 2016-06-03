var kMarker_AnimationDuration_ChangeDrawable = 500;
var kMarker_AnimationDuration_Resize = 1000;

function Marker(poiData) {

    this.poiData = poiData;
    this.isSelected = false;

    /*
        With AR.PropertyAnimations you are able to animate almost any property of ARchitect objects. This sample will animate the opacity of both background drawables so that one will fade out while the other one fades in. The scaling is animated too. The marker size changes over time so the labels need to be animated too in order to keep them relative to the background drawable. AR.AnimationGroups are used to synchronize all animations in parallel or sequentially.
    */

    this.animationGroup_idle = null;
    this.animationGroup_selected = null;


    // create the AR.GeoLocation from the poi data
    var markerLocation = new AR.GeoLocation(poiData.latitude, poiData.longitude, poiData.altitude);

    // create an AR.ImageDrawable for the marker in idle state
    this.markerDrawable_idle = new AR.ImageDrawable(World.markerDrawable_idle, 2.5, {
        zOrder: 0,
        opacity: 1.0,
        /*
            To react on user interaction, an onClick property can be set for each AR.Drawable. The property is a function which will be called each time the user taps on the drawable. The function called on each tap is returned from the following helper function defined in marker.js. The function returns a function which checks the selected state with the help of the variable isSelected and executes the appropriate function. The clicked marker is passed as an argument.
        */
        onClick: Marker.prototype.getOnClickTrigger(this)
    });

    // create an AR.ImageDrawable for the marker in selected state
    this.markerDrawable_selected = new AR.ImageDrawable(World.markerDrawable_selected, 2.5, {
        zOrder: 0,
        opacity: 0.0,
        onClick: null
    });

    // create an AR.Label for the marker's title 
    this.titleLabel = new AR.Label(poiData.title.trunc(10), 1, {
        zOrder: 1,
        offsetY: 0.55,
        style: {
            textColor: '#FFFFFF',
            fontStyle: AR.CONST.FONT_STYLE.BOLD
        }
    });

    // create an AR.Label for the marker's description
    this.descriptionLabel = new AR.Label(poiData.description.trunc(15), 0.8, {
        zOrder: 1,
        offsetY: -0.55,
        style: {
            textColor: '#FFFFFF'
        }
    });

    /*
        Create an AR.ImageDrawable using the AR.ImageResource for the direction indicator which was created in the World. Set options regarding the offset and anchor of the image so that it will be displayed correctly on the edge of the screen.
    */
    this.directionIndicatorDrawable = new AR.ImageDrawable(World.markerDrawable_directionIndicator, 0.1, {
        enabled: false,
        verticalAnchor: AR.CONST.VERTICAL_ANCHOR.TOP
    });

    /*
        Create the AR.GeoObject with the drawable objects and define the AR.ImageDrawable as an indicator target on the marker AR.GeoObject. The direction indicator is displayed automatically when necessary. AR.Drawable subclasses (e.g. AR.Circle) can be used as direction indicators.
    */
    this.markerObject = new AR.GeoObject(markerLocation, {
        drawables: {
            cam: [this.markerDrawable_idle, this.markerDrawable_selected, this.titleLabel, this.descriptionLabel],
            indicator: this.directionIndicatorDrawable
        }
    });

    return this;
}

Marker.prototype.getOnClickTrigger = function(marker) {

    /*
        The setSelected and setDeselected functions are prototype Marker functions. 
        Both functions perform the same steps but inverted.
    */

    return function() {

        if (!Marker.prototype.isAnyAnimationRunning(marker)) {
            if (marker.isSelected) {

                Marker.prototype.setDeselected(marker);

            } else {
                Marker.prototype.setSelected(marker);
                try {
                    World.onMarkerSelected(marker);
                } catch (err) {
                    alert(err);
                }

            }
        } else {
            AR.logger.debug('a animation is already running');
        }


        return true;
    };
};

/*
    Property Animations allow constant changes to a numeric value/property of an object, dependent on start-value, end-value and the duration of the animation. Animations can be seen as functions defining the progress of the change on the value. The Animation can be parametrized via easing curves.
*/

Marker.prototype.setSelected = function(marker) {

    marker.isSelected = true;

    // New: 
    if (marker.animationGroup_selected === null) {

        // create AR.PropertyAnimation that animates the opacity to 0.0 in order to hide the idle-state-drawable
        var hideIdleDrawableAnimation = new AR.PropertyAnimation(marker.markerDrawable_idle, "opacity", null, 0.0, kMarker_AnimationDuration_ChangeDrawable);
        // create AR.PropertyAnimation that animates the opacity to 1.0 in order to show the selected-state-drawable
        var showSelectedDrawableAnimation = new AR.PropertyAnimation(marker.markerDrawable_selected, "opacity", null, 1.0, kMarker_AnimationDuration_ChangeDrawable);

        // create AR.PropertyAnimation that animates the scaling of the idle-state-drawable to 1.2
        var idleDrawableResizeAnimation = new AR.PropertyAnimation(marker.markerDrawable_idle, 'scaling', null, 1.2, kMarker_AnimationDuration_Resize, new AR.EasingCurve(AR.CONST.EASING_CURVE_TYPE.EASE_OUT_ELASTIC, {
            amplitude: 2.0
        }));
        // create AR.PropertyAnimation that animates the scaling of the selected-state-drawable to 1.2
        var selectedDrawableResizeAnimation = new AR.PropertyAnimation(marker.markerDrawable_selected, 'scaling', null, 1.2, kMarker_AnimationDuration_Resize, new AR.EasingCurve(AR.CONST.EASING_CURVE_TYPE.EASE_OUT_ELASTIC, {
            amplitude: 2.0
        }));
        // create AR.PropertyAnimation that animates the scaling of the title label to 1.2
        var titleLabelResizeAnimation = new AR.PropertyAnimation(marker.titleLabel, 'scaling', null, 1.2, kMarker_AnimationDuration_Resize, new AR.EasingCurve(AR.CONST.EASING_CURVE_TYPE.EASE_OUT_ELASTIC, {
            amplitude: 2.0
        }));
        // create AR.PropertyAnimation that animates the scaling of the description label to 1.2
        var descriptionLabelResizeAnimation = new AR.PropertyAnimation(marker.descriptionLabel, 'scaling', null, 1.2, kMarker_AnimationDuration_Resize, new AR.EasingCurve(AR.CONST.EASING_CURVE_TYPE.EASE_OUT_ELASTIC, {
            amplitude: 2.0
        }));

        /*
            There are two types of AR.AnimationGroups. Parallel animations are running at the same time, sequentials are played one after another. This example uses a parallel AR.AnimationGroup.
        */
        marker.animationGroup_selected = new AR.AnimationGroup(AR.CONST.ANIMATION_GROUP_TYPE.PARALLEL, [hideIdleDrawableAnimation, showSelectedDrawableAnimation, idleDrawableResizeAnimation, selectedDrawableResizeAnimation, titleLabelResizeAnimation, descriptionLabelResizeAnimation]);
    }

    // removes function that is set on the onClick trigger of the idle-state marker
    marker.markerDrawable_idle.onClick = null;
    // sets the click trigger function for the selected state marker
    marker.markerDrawable_selected.onClick = Marker.prototype.getOnClickTrigger(marker);

    // enables the direction indicator drawable for the current marker
    marker.directionIndicatorDrawable.enabled = true;
    // starts the selected-state animation
    marker.animationGroup_selected.start();
};

Marker.prototype.setDeselected = function(marker) {

    marker.isSelected = false;

    if (marker.animationGroup_idle === null) {

        // create AR.PropertyAnimation that animates the opacity to 1.0 in order to show the idle-state-drawable
        var showIdleDrawableAnimation = new AR.PropertyAnimation(marker.markerDrawable_idle, "opacity", null, 1.0, kMarker_AnimationDuration_ChangeDrawable);
        // create AR.PropertyAnimation that animates the opacity to 0.0 in order to hide the selected-state-drawable
        var hideSelectedDrawableAnimation = new AR.PropertyAnimation(marker.markerDrawable_selected, "opacity", null, 0, kMarker_AnimationDuration_ChangeDrawable);
        // create AR.PropertyAnimation that animates the scaling of the idle-state-drawable to 1.0
        var idleDrawableResizeAnimation = new AR.PropertyAnimation(marker.markerDrawable_idle, 'scaling', null, 1.0, kMarker_AnimationDuration_Resize, new AR.EasingCurve(AR.CONST.EASING_CURVE_TYPE.EASE_OUT_ELASTIC, {
            amplitude: 2.0
        }));
        // create AR.PropertyAnimation that animates the scaling of the selected-state-drawable to 1.0
        var selectedDrawableResizeAnimation = new AR.PropertyAnimation(marker.markerDrawable_selected, 'scaling', null, 1.0, kMarker_AnimationDuration_Resize, new AR.EasingCurve(AR.CONST.EASING_CURVE_TYPE.EASE_OUT_ELASTIC, {
            amplitude: 2.0
        }));
        // create AR.PropertyAnimation that animates the scaling of the title label to 1.0
        var titleLabelResizeAnimation = new AR.PropertyAnimation(marker.titleLabel, 'scaling', null, 1.0, kMarker_AnimationDuration_Resize, new AR.EasingCurve(AR.CONST.EASING_CURVE_TYPE.EASE_OUT_ELASTIC, {
            amplitude: 2.0
        }));
        // create AR.PropertyAnimation that animates the scaling of the description label to 1.0
        var descriptionLabelResizeAnimation = new AR.PropertyAnimation(marker.descriptionLabel, 'scaling', null, 1.0, kMarker_AnimationDuration_Resize, new AR.EasingCurve(AR.CONST.EASING_CURVE_TYPE.EASE_OUT_ELASTIC, {
            amplitude: 2.0
        }));

        /*
            There are two types of AR.AnimationGroups. Parallel animations are running at the same time, sequentials are played one after another. This example uses a parallel AR.AnimationGroup.
        */
        marker.animationGroup_idle = new AR.AnimationGroup(AR.CONST.ANIMATION_GROUP_TYPE.PARALLEL, [showIdleDrawableAnimation, hideSelectedDrawableAnimation, idleDrawableResizeAnimation, selectedDrawableResizeAnimation, titleLabelResizeAnimation, descriptionLabelResizeAnimation]);
    }

    // sets the click trigger function for the idle state marker
    marker.markerDrawable_idle.onClick = Marker.prototype.getOnClickTrigger(marker);
    // removes function that is set on the onClick trigger of the selected-state marker
    marker.markerDrawable_selected.onClick = null;

    // disables the direction indicator drawable for the current marker
    marker.directionIndicatorDrawable.enabled = false;
    // starts the idle-state animation
    marker.animationGroup_idle.start();
};

Marker.prototype.isAnyAnimationRunning = function(marker) {

    if (marker.animationGroup_idle === null || marker.animationGroup_selected === null) {
        return false;
    } else {
        if ((marker.animationGroup_idle.isRunning() === true) || (marker.animationGroup_selected.isRunning() === true)) {
            return true;
        } else {
            return false;
        }
    }
};

// will truncate all strings longer than given max-length "n". e.g. "foobar".trunc(3) -> "foo..."
String.prototype.trunc = function(n) {
    return this.substr(0, n - 1) + (this.length > n ? '...' : '');
};

// implementation of AR-Experience (aka "World")
var World = {
	// you may request new data from server periodically, however: in this sample data is only requested once
	isRequestingData: false,

	// true once data was fetched
	initiallyLoadedData: false,

	// different POI-Marker assets
	markerDrawable_idle: null,
	markerDrawable_selected: null,
	markerDrawable_directionIndicator: null,

	// list of AR.GeoObjects that are currently shown in the scene / World
	markerList: [],

	// The last selected marker
	currentMarker: null,

	// called to inject new POI data
	loadPoisFromJsonData: function loadPoisFromJsonDataFn(poiData) {
		// empty list of visible markers
		World.markerList = [];

		// Start loading marker assets:
		// Create an AR.ImageResource for the marker idle-image
		World.markerDrawable_idle = new AR.ImageResource("assets/marker_idle.png");
		// Create an AR.ImageResource for the marker selected-image
		World.markerDrawable_selected = new AR.ImageResource("assets/marker_selected.png");
		// Create an AR.ImageResource referencing the image that should be displayed for a direction indicator.
		World.markerDrawable_directionIndicator = new AR.ImageResource("assets/indi.png");

		// loop through POI-information and create an AR.GeoObject (=Marker) per POI
		for (var currentPlaceNr = 0; currentPlaceNr < poiData.length; currentPlaceNr++) {
			var singlePoi = {
				"id": poiData[currentPlaceNr].id,
				"latitude": parseFloat(poiData[currentPlaceNr].latitude),
				"longitude": parseFloat(poiData[currentPlaceNr].longitude),
				"altitude": parseFloat(poiData[currentPlaceNr].altitude),
				"title": poiData[currentPlaceNr].name,
				"description": poiData[currentPlaceNr].description
			};

			/*
				To be able to deselect a marker while the user taps on the empty screen,
				the World object holds an array that contains each marker.
			*/
			World.markerList.push(new Marker(singlePoi));
		}

		World.updateStatusMessage(currentPlaceNr + ' places loaded');
	},

	// updates status message shon in small "i"-button aligned bottom center
	updateStatusMessage: function updateStatusMessageFn(message, isWarning) {

		var themeToUse = isWarning ? "e" : "c";
		var iconToUse = isWarning ? "alert" : "info";

		$("#status-message").html(message);
		$("#popupInfoButton").buttonMarkup({
			theme: themeToUse
		});
		$("#popupInfoButton").buttonMarkup({
			icon: iconToUse
		});
	},

	// location updates, fired every time you call architectView.setLocation() in native environment
	locationChanged: function locationChangedFn(lat, lon, alt, acc) {

		/*
			The custom function World.onLocationChanged checks with the flag World.initiallyLoadedData if the function was already called. With the first call of World.onLocationChanged an object that contains geo information will be created which will be later used to create a marker using the World.loadPoisFromJsonData function.
		*/
		if (!World.initiallyLoadedData) {
			/*
				requestDataFromLocal with the geo information as parameters (latitude, longitude) creates different poi data to a random location in the user's vicinity.
			*/
			World.requestDataFromLocal(lat, lon);
			World.initiallyLoadedData = true;
		}
	},

	// fired when user pressed maker in cam
	onMarkerSelected: function onMarkerSelectedFn(marker) {

		// deselect previous marker
		if (World.currentMarker) {
			if (World.currentMarker.poiData.id == marker.poiData.id) {
				return;
			}
			World.currentMarker.setDeselected(World.currentMarker);
		}

		// highlight current one
		marker.setSelected(marker);
		World.currentMarker = marker;
	},

	// screen was clicked but no geo-object was hit
	onScreenClick: function onScreenClickFn() {
		if (World.currentMarker) {
			World.currentMarker.setDeselected(World.currentMarker);
		}
	},

	// request POI data
	requestDataFromLocal: function requestDataFromLocalFn(centerPointLatitude, centerPointLongitude) {
		var poisToCreate = 2;
		var poiData = [];
		var longVals = ["-79.942840","-79.949803"];
		var latVals = ["40.444265", "40.447294"];
		for (var i = 0; i < poisToCreate; i++) {
			poiData.push({
				"id": (1),
//				"longitude": (centerPointLongitude + (Math.random() / 5 - 0.1)),
//				"latitude": (centerPointLatitude + (Math.random() / 5 - 0.1)),

//				"longitude": (-79.946798),
//                "latitude": (40.437226),
				"longitude": longVals[i],
                "latitude": latVals[i],
				"description": ("This is the description of POI#" + (1)),
				// use this value to ignore altitude information in general - marker will always be on user-level
				"altitude": AR.CONST.UNKNOWN_ALTITUDE,
				"name": ("POI#" + (1))
			});



		}
		World.loadPoisFromJsonData(poiData);
	}

};

/*
	Set a custom function where location changes are forwarded to. There is also a possibility to set AR.context.onLocationChanged to null. In this case the function will not be called anymore and no further location updates will be received.
*/
AR.context.onLocationChanged = World.locationChanged;

/*
	To detect clicks where no drawable was hit set a custom function on AR.context.onScreenClick where the currently selected marker is deselected.
*/
AR.context.onScreenClick = World.onScreenClick;