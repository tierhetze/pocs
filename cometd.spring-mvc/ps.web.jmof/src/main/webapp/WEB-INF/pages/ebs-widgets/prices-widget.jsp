<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<div id="price_widget">
    
    <div id="widget_title">Prices Widget</div>
    
    <div id="prices_eurusd"></div>
    
    <div id="prices_usdjpy"></div>
    
    <div id="input">
        <div id="join">
            <table>
                <tbody>
                <tr>
                    <td>&nbsp;</td>
                    <td>&nbsp;</td>
                    <td>
                        <input id="prices_username" type="text" size="3" value="${user.username}" disabled/>
                    </td>
                    <td>
                        <button id="prices_joinButton" class="button">Get Prices</button>
                    </td>
                </tr>
                <tr>
                    <td>&nbsp;</td>
                    <td>
                        
                    </td>
                    <td>
                        
                    </td>
                    <td>
                        <button id="prices_disconnectButton" class="button">Stop Prices</button>
                    </td>
                </tr>
                </tbody>
            </table>
        </div>
    </div>
</div>