<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<div id="deals_widget">
    
    <div id="widget_title">Deals Widget</div>
    
    <div id="deals_eurusd">
        
    
    </div>
    
    <div id="deals_usdjpy">
    
    
    </div>
    
    <div id="input">
        <div id="join">
            <table>
                <tbody>
                <tr>
                    <td>&nbsp;</td>
                    <td>&nbsp;</td>
                    <td>
                        <input id="deals_username" type="text" size="3" value="${user.username}" disabled/>
                    </td>
                    <td>
                        <button id="deals_joinButton" class="button">Get Deals</button>
                    </td>
                </tr>
                <tr>
                    <td>&nbsp;</td>
                    <td>
                        
                    </td>
                    <td>
                        
                    </td>
                    <td>
                        <button id="deals_disconnectButton" class="button">Stop Deals</button>
                    </td>
                </tr>
                </tbody>
            </table>
        </div>
    </div>
</div>