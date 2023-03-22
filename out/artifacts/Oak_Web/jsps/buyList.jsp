<html lang="en"><head>
    <meta charset="UTF-8">
    <title>User</title>
    <style>
        li {
        	padding: 5px
        }
        table{
            width: 100%;
            text-align: center;
        }
    </style>
</head>
<body>
    <ul>
        <li id="username">Username: Farhad</li>
        <li id="email">Email: Farhad@gmail.com</li>
        <li id="birthDate">Birth Date: 2000/07/21</li>
        <li id="address">Tehran, North Karegar, No 10</li>
        <li id="credit">Credit: 700000</li>
        <li>Current Buy List Price: 64000000</li>
        <li>
            <a href="/credit">Add Credit</a>
        </li>
        <li>
            <form action="" method="POST">
                <label>Submit & Pay</label>
                <input id="form_payment" type="hidden" name="userId" value="Farhad">
                <button type="submit">Payment</button>
            </form>
        </li>
    </ul>
    <table>
        <caption>
            <h2>Buy List</h2>
        </caption>
        <tbody><tr>
            <th>Id</th> 
            <th>Name</th> 
            <th>Provider Name</th> 
            <th>Price</th> 
            <th>Categories</th> 
            <th>Rating</th> 
            <th>In Stock</th>
            <th></th>
            <th></th>
        </tr>
        <tr>
            <td>4231</td>
            <td>Galaxy S22 Plus</td> 
            <td>Phone Provider No.1</td>
            <td>43000000</td>
            <td>Technology, Phone</td>
            <td>8.7</td>
            <td>12</td>
            <td><a href="/commodities/4231">Link</a></td>
            <td>        
                <form action="" method="POST">
                    <input id="form_commodity_id" type="hidden" name="commodityId" value="4231">
                    <button type="submit">Remove</button>
                </form>
            </td>
        </tr>
        <tr>
            <th>2341</th> 
            <th>Galaxy S21</th>
            <th>Phone Provider No.2</th> 
            <th>21000000</th> 
            <th>Technology, Phone</th> 
            <th>8.3</th> 
            <th>17</th> 
            <td><a href="/commodities/2341">Link</a></td>
            <td>        
                <form action="" method="POST">
                    <input id="form_commodity_id" type="hidden" name="commodityId" value="2341">
                    <button type="submit">Remove</button>
                </form>
            </td>
        </tr>
    </tbody></table>
</body></html>