
@app.route('/get_recommended_follows', methods=['POST'])
def get_recommended_follows():
	try:
		email = request.form['email_user']
		limit = request.form['limit']
		conn = MySQLdb.connect(host='localhost', user='muscle', password='some_pass', database='muscle')
		c = conn.cursor(MySQLdb.cursors.DictCursor)
		
		c.execute('USE muscle2')
		c.execute("""
				SELECT Following, count(Following) AS count
				FROM FOLLOWERS
				JOIN (SELECT Following
					FROM FOLLOWERS
					WHERE User_email = %s) AS t
				ON FOLLOWERS.User_email = t.Following
				GROUP BY Following
				ORDER BY count DESC
				LIMIT %s
				""", [email, limit])

		r = c.fetchall()
		c.close()
		conn.close()
		return jsonify(r) 

	except Exception as e:
		return str(e)
