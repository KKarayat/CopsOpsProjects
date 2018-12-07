<?php

use Illuminate\Http\Request;

/*
|--------------------------------------------------------------------------
| API Routes
|--------------------------------------------------------------------------
|
| Here is where you can register API routes for your application. These
| routes are loaded by the RouteServiceProvider within a group which
| is assigned the "api" middleware group. Enjoy building your API!
|
*/

#Route::middleware('auth')->post('/signup', 'ApiController@register');

Route::group(['prefix' => 'auth'], function () {

    Route::post('/incidents', 'API\ApiController@get_incidents');
    Route::post('/sub/incidents', 'API\ApiController@get_incident_subcategory');

    Route::post('register/incident',  'API\ApiController@report_incident');
    Route::post('register/handrail',  'API\ApiController@report_handrail');

    Route::post('/register', 'API\ApiController@register');
    Route::post('/validate/otp', 'API\ApiController@validate_otp');

    Route::post('/login', 'API\ApiController@login');
    Route::post('/password/reset', 'API\ApiController@reset_password');

    Route::post('/upload/image', 'API\ApiController@upload_image');
    Route::post('/test/test', 'API\ApiController@parseJSON');
	
	Route::post('/profileupdate', 'API\ApiController@profileupdate');
});

//Route::middleware('auth:api')->get('/user', function (Request $request) {
//    return $request->user();
//});