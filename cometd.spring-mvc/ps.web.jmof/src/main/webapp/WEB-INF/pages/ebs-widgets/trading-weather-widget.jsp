<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<div id="trading_weather_widget">
    
    <div id="widget_title">Trading Weather Combined Widget</div>
    <div id="weather_prices_eurusd"></div>
    <div id="weather_prices_usdjpy"></div>
    <div id="weather"></div>
    <div id="input">
        <div id="join">
            <table>
                <tbody>
                <tr>
                    <td>&nbsp;</td>
                    
                    <td>&nbsp;</td>
                    <td>&nbsp;</td>
                    <td>
                        <input id="weather_prices_username" type="text" size="3" value="${user.username}" disabled/>
                    </td>
                    <td>
                        <button id="weather_prices_joinButton" class="button">Get Weather &amp; Prices</button>
                    </td>
                </tr>
                <tr>
                    <td>&nbsp;</td>
                    
                    <td>&nbsp;</td>
                    <td>
                        
                    </td>
                    <td>
                        
                    </td>
                    <td>
                        <button id="weather_prices_disconnectButton" class="button">Stop Weather &amp; Prices</button>
                    </td>
                </tr>
                </tbody>
            </table>
        </div>
    </div>
</div>